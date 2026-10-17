package site.siredvin.broccolium.modules.data

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableSubProvider
import net.minecraft.client.data.models.BlockModelGenerators
import net.minecraft.client.data.models.ItemModelGenerators
import net.minecraft.core.HolderLookup
import net.minecraft.core.RegistrySetBuilder
import net.minecraft.data.DataProvider
import net.minecraft.data.loot.LootTableProvider
import net.minecraft.data.tags.TagsProvider
import net.minecraft.resources.ResourceKey
import net.minecraft.tags.TagKey
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.storage.loot.LootTable
import site.siredvin.broccolium.modules.data.api.*
import site.siredvin.broccolium.modules.data.model.ModelProvider
import site.siredvin.broccolium.modules.platform.PlatformRegistries
import java.util.concurrent.CompletableFuture
import java.util.function.BiConsumer
import java.util.function.Consumer
import java.util.function.Function

class FabricGeneratorSink(private val pack: FabricDataGenerator.Pack, private val registryLookup: CompletableFuture<HolderLookup.Provider>) : GeneratorSink {
    override fun <T : DataProvider> add(factory: TweakedDataProviderFactory<T>): T = pack.addProvider(DataProvider.Factory { factory.create(it, registryLookup) })
    override fun <T : DataProvider> add(factory: DataProvider.Factory<T>): T = pack.addProvider(factory)

    override fun addRegistryPatch(name: String, factory: Function<CompletableFuture<HolderLookup.Provider>, CompletableFuture<RegistrySetBuilder.PatchedRegistries>>) {
        pack.addProvider(FabricDataGenerator.Pack.Factory { AutomaticDynamicRegistryProvider(name, it, factory.apply(registryLookup)) })
    }

    override fun lootTable(tables: List<LootTableProvider.SubProviderEntry>) {
        tables.forEach { entry ->
            pack.addProvider { out: FabricPackOutput ->
                object : SimpleFabricLootTableSubProvider(out, registryLookup, entry.paramSet) {
                    override fun generate(consumer: BiConsumer<ResourceKey<LootTable>, LootTable.Builder>) {
                        entry.provider.apply(registryLookup.join()).generate(consumer)
                    }
                }
            }
        }
    }

    override fun blockTags(modID: String, tags: Consumer<TagConsumer<Block>>): TagsProvider<Block> = pack.addProvider { out, registries ->
        object : FabricTagsProvider.BlockTagsProvider(out, registries) {
            override fun addTags(registries: HolderLookup.Provider) {
                tags.accept { tag -> LibTagAppender(PlatformRegistries.BLOCKS, getOrCreateRawBuilder(tag)) }
            }
        }
    }

    override fun entityTags(modID: String, tags: Consumer<TagConsumer<EntityType<*>>>): TagsProvider<EntityType<*>> = pack.addProvider { out, registries ->
        object : FabricTagsProvider.EntityTypeTagsProvider(out, registries) {
            override fun addTags(registries: HolderLookup.Provider) {
                tags.accept { tag -> LibTagAppender(PlatformRegistries.ENTITY_TYPES, getOrCreateRawBuilder(tag)) }
            }
        }
    }

    override fun itemTags(modID: String, tags: Consumer<ItemTagConsumer>, blocks: TagsProvider<Block>): TagsProvider<Item> = pack.addProvider { out, registries ->
        object : FabricTagsProvider.ItemTagsProvider(out, registries, blocks as FabricTagsProvider.BlockTagsProvider) {
            override fun addTags(registries: HolderLookup.Provider) {
                val self = this
                tags.accept(object : ItemTagConsumer {
                    override fun tag(tag: TagKey<Item>): LibTagAppender<Item> = LibTagAppender(PlatformRegistries.ITEMS, getOrCreateRawBuilder(tag))

                    override fun copy(block: TagKey<Block>, item: TagKey<Item>) {
                        self.copy(block, item)
                    }
                })
            }
        }
    }

    override fun models(blocks: Consumer<BlockModelGenerators>, items: Consumer<ItemModelGenerators>) {
        add(DataProvider.Factory { ModelProvider(it, blocks, items) })
    }
}
