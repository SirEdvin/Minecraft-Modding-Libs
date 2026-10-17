package site.siredvin.broccolium.modules.data.model

import net.minecraft.client.data.models.BlockModelGenerators
import net.minecraft.client.data.models.ItemModelGenerators
import net.minecraft.client.data.models.ItemModelOutput
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator
import net.minecraft.client.data.models.model.ItemModelUtils
import net.minecraft.client.data.models.model.ModelInstance
import net.minecraft.client.data.models.model.ModelLocationUtils
import net.minecraft.client.renderer.block.dispatch.BlockStateModelDispatcher
import net.minecraft.client.renderer.item.ClientItem
import net.minecraft.client.renderer.item.ItemModel
import net.minecraft.data.CachedOutput
import net.minecraft.data.DataProvider
import net.minecraft.data.PackOutput
import net.minecraft.resources.Identifier
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import site.siredvin.broccolium.modules.platform.PlatformRegistries
import java.util.concurrent.CompletableFuture
import java.util.function.BiConsumer
import java.util.function.Consumer

/** Native model collectors scoped to this provider's content, not the entire vanilla registry. */
class ModelProvider(
    output: PackOutput,
    private val blocks: Consumer<BlockModelGenerators>,
    private val items: Consumer<ItemModelGenerators>,
) : DataProvider {
    private val blockStatePath = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "blockstates")
    private val modelPath = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models")
    private val itemPath = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "items")

    override fun run(output: CachedOutput): CompletableFuture<*> {
        val blockStates = linkedMapOf<Block, BlockModelDefinitionGenerator>()
        val models = linkedMapOf<Identifier, ModelInstance>()
        val itemInfos = linkedMapOf<Item, ClientItem>()
        val copies = linkedMapOf<Item, Item>()
        val addBlockState = Consumer<BlockModelDefinitionGenerator> { generator ->
            val block = generator.block()
            check(blockStates.putIfAbsent(block, generator) == null) { "Duplicate blockstate definition for $block" }
        }
        val addModel = BiConsumer<Identifier, ModelInstance> { id, contents ->
            check(models.putIfAbsent(id, contents) == null) { "Duplicate model definition for $id" }
        }
        val itemOutput = object : ItemModelOutput {
            override fun accept(item: Item, model: ItemModel.Unbaked, properties: ClientItem.Properties) {
                check(!copies.containsKey(item) && itemInfos.putIfAbsent(item, ClientItem(model, properties)) == null) {
                    "Duplicate item definition for $item"
                }
            }

            override fun copy(source: Item, target: Item) {
                check(!itemInfos.containsKey(target) && copies.putIfAbsent(target, source) == null) {
                    "Duplicate item definition for $target"
                }
            }
        }
        blocks.accept(BlockModelGenerators(addBlockState, itemOutput, addModel))
        items.accept(ItemModelGenerators(itemOutput, addModel))
        for (block in blockStates.keys) {
            val item = Item.BY_BLOCK[block] ?: continue
            if (!itemInfos.containsKey(item) && !copies.containsKey(item)) {
                itemOutput.accept(item, ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(block)))
            }
        }
        val pending = copies.toMutableMap()
        while (pending.isNotEmpty()) {
            val resolved = pending.filterValues(itemInfos::containsKey)
            check(resolved.isNotEmpty()) { "Unresolved or cyclic item model copies: $pending" }
            for ((target, source) in resolved) {
                itemInfos[target] = itemInfos.getValue(source)
                pending.remove(target)
            }
        }
        val futures = mutableListOf<CompletableFuture<*>>()
        futures.add(DataProvider.saveAll(output, BlockStateModelDispatcher.CODEC, { block: Block -> blockStatePath.json(PlatformRegistries.BLOCKS.getKey(block)) }, blockStates.mapValues { it.value.create() }))
        futures.add(DataProvider.saveAll(output, ClientItem.CODEC, { item: Item -> itemPath.json(PlatformRegistries.ITEMS.getKey(item)) }, itemInfos))
        models.forEach { (id, model) -> futures.add(DataProvider.saveStable(output, model.get(), modelPath.json(id))) }
        return CompletableFuture.allOf(*futures.toTypedArray())
    }

    override fun getName(): String = "Block State Definitions"
}
