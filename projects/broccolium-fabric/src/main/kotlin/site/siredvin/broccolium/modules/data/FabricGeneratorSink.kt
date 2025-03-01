package site.siredvin.broccolium.modules.data

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider
import net.minecraft.core.HolderLookup
import net.minecraft.data.DataProvider
import net.minecraft.data.loot.LootTableProvider
import net.minecraft.data.models.BlockModelGenerators
import net.minecraft.data.models.ItemModelGenerators
import net.minecraft.data.tags.TagsProvider
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.storage.loot.LootTable
import site.siredvin.broccolium.modules.data.api.GeneratorSink
import site.siredvin.broccolium.modules.data.api.ItemTagConsumer
import site.siredvin.broccolium.modules.data.api.LibTagAppender
import site.siredvin.broccolium.modules.data.api.TagConsumer
import site.siredvin.broccolium.modules.data.model.ModelProvider
import site.siredvin.broccolium.modules.platform.PlatformRegistries
import java.util.function.BiConsumer
import java.util.function.Consumer

class FabricGeneratorSink(private val pack: FabricDataGenerator.Pack) : GeneratorSink {
    override fun <T : DataProvider> add(factory: DataProvider.Factory<T>): T = pack.addProvider(factory)

    override fun lootTable(tables: List<LootTableProvider.SubProviderEntry>) {
        tables.forEach {
            pack.addProvider { out: FabricDataOutput ->
                object : SimpleFabricLootTableProvider(out, it.paramSet) {
                    override fun generate(consumer: BiConsumer<ResourceLocation, LootTable.Builder>) {
                        it.provider.get().generate(consumer)
                    }
                }
            }
        }
    }

    override fun blockTags(modID: String, tags: Consumer<TagConsumer<Block>>): TagsProvider<Block> = pack.addProvider { out, registries ->
        object : FabricTagProvider.BlockTagProvider(out, registries) {
            override fun addTags(registries: HolderLookup.Provider) {
                tags.accept { x -> LibTagAppender(PlatformRegistries.BLOCKS, getOrCreateRawBuilder(x)) }
            }
        }
    }

    override fun entityTags(modID: String, tags: Consumer<TagConsumer<EntityType<*>>>): TagsProvider<EntityType<*>> = pack.addProvider { out, registries ->
        object : FabricTagProvider.EntityTypeTagProvider(out, registries) {
            override fun addTags(arg: HolderLookup.Provider) {
                tags.accept { x -> LibTagAppender(PlatformRegistries.ENTITY_TYPES, getOrCreateRawBuilder(x)) }
            }
        }
    }

    override fun itemTags(modID: String, tags: Consumer<ItemTagConsumer>, blocks: TagsProvider<Block>): TagsProvider<Item> = pack.addProvider { out, registries ->
        object : FabricTagProvider.ItemTagProvider(out, registries, blocks as BlockTagProvider) {
            override fun addTags(registries: HolderLookup.Provider) {
                val self: ItemTagProvider = this
                tags.accept(object : ItemTagConsumer {
                    override fun tag(tag: TagKey<Item>): LibTagAppender<Item> = LibTagAppender(PlatformRegistries.ITEMS, getOrCreateRawBuilder(tag))

                    override fun copy(
                        block: TagKey<Block>,
                        item: TagKey<Item>,
                    ) {
                        self.copy(block, item)
                    }
                })
            }
        }
    }

    override fun models(blocks: Consumer<BlockModelGenerators>, items: Consumer<ItemModelGenerators>) {
        add { ModelProvider(it, blocks, items) }
    }
}
