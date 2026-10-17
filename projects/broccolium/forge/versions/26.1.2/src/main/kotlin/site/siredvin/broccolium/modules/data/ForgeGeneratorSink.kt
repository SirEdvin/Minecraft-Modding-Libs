package site.siredvin.broccolium.modules.data

import net.minecraft.client.data.models.BlockModelGenerators
import net.minecraft.client.data.models.ItemModelGenerators
import net.minecraft.core.HolderLookup
import net.minecraft.core.RegistrySetBuilder
import net.minecraft.data.DataGenerator
import net.minecraft.data.DataProvider
import net.minecraft.data.loot.LootTableProvider
import net.minecraft.data.tags.EntityTypeTagsProvider
import net.minecraft.data.tags.TagsProvider
import net.minecraft.tags.TagKey
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.neoforged.neoforge.common.data.BlockTagsProvider
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider
import net.neoforged.neoforge.common.data.ItemTagsProvider
import net.neoforged.neoforge.data.event.GatherDataEvent
import site.siredvin.broccolium.modules.data.api.*
import site.siredvin.broccolium.modules.data.model.ModelProvider
import site.siredvin.broccolium.modules.platform.PlatformRegistries
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer
import java.util.function.Function

class ForgeGeneratorSink(private val generator: DataGenerator, private val event: GatherDataEvent) : GeneratorSink {
    private val registries: CompletableFuture<HolderLookup.Provider> = event.lookupProvider

    override fun <T : DataProvider> add(factory: TweakedDataProviderFactory<T>): T = generator.addProvider(true, DataProvider.Factory { factory.create(it, registries) })
    override fun <T : DataProvider> add(factory: DataProvider.Factory<T>): T = generator.addProvider(true, factory)

    override fun addRegistryPatch(name: String, factory: Function<CompletableFuture<HolderLookup.Provider>, CompletableFuture<RegistrySetBuilder.PatchedRegistries>>) {
        add(DataProvider.Factory { output -> DatapackBuiltinEntriesProvider(output, factory.apply(registries), setOf(name)) })
    }

    override fun lootTable(tables: List<LootTableProvider.SubProviderEntry>) {
        add(DataProvider.Factory { output -> LootTableProvider(output, setOf(), tables, registries) })
    }

    override fun blockTags(modID: String, tags: Consumer<TagConsumer<Block>>): TagsProvider<Block> = add(DataProvider.Factory { output ->
        object : BlockTagsProvider(output, registries, modID) {
            override fun addTags(registries: HolderLookup.Provider) {
                tags.accept { tag -> LibTagAppender(PlatformRegistries.BLOCKS, getOrCreateRawBuilder(tag)) }
            }
        }
    })

    override fun entityTags(modID: String, tags: Consumer<TagConsumer<EntityType<*>>>): TagsProvider<EntityType<*>> = add(DataProvider.Factory { output ->
        object : EntityTypeTagsProvider(output, registries, modID) {
            override fun addTags(registries: HolderLookup.Provider) {
                tags.accept { tag -> LibTagAppender(PlatformRegistries.ENTITY_TYPES, getOrCreateRawBuilder(tag)) }
            }
        }
    })

    override fun itemTags(modID: String, tags: Consumer<ItemTagConsumer>, blocks: TagsProvider<Block>): TagsProvider<Item> = add(DataProvider.Factory { output ->
        object : ItemTagsProvider(output, registries, modID) {
            private lateinit var blockTags: TagsProvider.TagLookup<Block>

            override fun createContentsProvider(): CompletableFuture<HolderLookup.Provider> = registries.thenCombine(blocks.contentsGetter()) { lookup, blockLookup ->
                blockTags = blockLookup
                builders.clear()
                addTags(lookup)
                lookup
            }

            override fun addTags(registries: HolderLookup.Provider) {
                tags.accept(object : ItemTagConsumer {
                    override fun tag(tag: TagKey<Item>): LibTagAppender<Item> = LibTagAppender(PlatformRegistries.ITEMS, getOrCreateRawBuilder(tag))

                    override fun copy(block: TagKey<Block>, item: TagKey<Item>) {
                        val source = blockTags.apply(block).orElseThrow { IllegalArgumentException("Cannot copy missing block tag $block") }
                        val destination = getOrCreateRawBuilder(item)
                        source.build().forEach(destination::add)
                    }
                })
            }
        }
    })

    override fun models(blocks: Consumer<BlockModelGenerators>, items: Consumer<ItemModelGenerators>) {
        // Register skipped providers too: their cache must survive the server
        // pass, otherwise its stale-file purge deletes the client models.
        generator.addProvider(event is GatherDataEvent.Client, DataProvider.Factory { ModelProvider(it, blocks, items) })
    }
}
