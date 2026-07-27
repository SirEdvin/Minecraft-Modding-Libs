package site.siredvin.broccolium.modules.data.api

import net.minecraft.data.DataProvider
import net.minecraft.data.loot.LootTableProvider
import net.minecraft.data.models.BlockModelGenerators
import net.minecraft.data.models.ItemModelGenerators
import net.minecraft.data.tags.TagsProvider
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import java.util.function.Consumer

interface GeneratorSink {
    fun <T : DataProvider> add(factory: DataProvider.Factory<T>): T
    fun lootTable(tables: List<LootTableProvider.SubProviderEntry>)
    fun blockTags(modID: String, tags: Consumer<TagConsumer<Block>>): TagsProvider<Block>

    fun entityTags(modID: String, tags: Consumer<TagConsumer<EntityType<*>>>): TagsProvider<EntityType<*>>
    fun itemTags(modID: String, tags: Consumer<ItemTagConsumer>, blocks: TagsProvider<Block>): TagsProvider<Item>
    fun models(blocks: Consumer<BlockModelGenerators>, items: Consumer<ItemModelGenerators>)
}
