package site.siredvin.broccolium.modules.data

import net.minecraft.core.HolderLookup
import net.minecraft.core.RegistrySetBuilder
import net.minecraft.data.DataGenerator
import net.minecraft.data.DataProvider
import net.minecraft.data.PackOutput
import net.minecraft.data.loot.LootTableProvider
import net.minecraft.data.models.BlockModelGenerators
import net.minecraft.data.models.ItemModelGenerators
import net.minecraft.data.tags.EntityTypeTagsProvider
import net.minecraft.data.tags.ItemTagsProvider
import net.minecraft.data.tags.TagsProvider
import net.minecraft.tags.TagKey
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.neoforged.neoforge.common.data.BlockTagsProvider
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider
import net.neoforged.neoforge.common.data.ExistingFileHelper
import net.neoforged.neoforge.data.event.GatherDataEvent
import site.siredvin.broccolium.modules.data.api.GeneratorSink
import site.siredvin.broccolium.modules.data.api.ItemTagConsumer
import site.siredvin.broccolium.modules.data.api.LibTagAppender
import site.siredvin.broccolium.modules.data.api.TagConsumer
import site.siredvin.broccolium.modules.data.api.TweakedDataProviderFactory
import site.siredvin.broccolium.modules.data.model.ModelProvider
import site.siredvin.broccolium.modules.platform.PlatformRegistries
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer
import java.util.function.Function

class ForgeGeneratorSink(private val generator: DataGenerator, private val event: GatherDataEvent) : GeneratorSink {
    private val registries: CompletableFuture<HolderLookup.Provider> = event.lookupProvider
    private val existingFiles: ExistingFileHelper = event.existingFileHelper

    override fun <T : DataProvider> add(factory: TweakedDataProviderFactory<T>): T = generator.addProvider(event.includeServer(), DataProvider.Factory { factory.create(it, registries) })

    override fun <T : DataProvider> add(factory: DataProvider.Factory<T>): T = generator.addProvider(event.includeServer(), factory)

    override fun addRegistryPatch(
        name: String,
        factory: Function<CompletableFuture<HolderLookup.Provider>, CompletableFuture<RegistrySetBuilder.PatchedRegistries>>,
    ) {
        generator.addProvider(
            event.includeServer(),
            DataProvider.Factory { out: PackOutput ->
                DatapackBuiltinEntriesProvider(out, factory.apply(registries), setOf(name))
            },
        )
    }

    override fun lootTable(tables: List<LootTableProvider.SubProviderEntry>) {
        add { out: PackOutput ->
            LootTableProvider(out, setOf(), tables, registries)
        }
    }

    override fun blockTags(modID: String, tags: Consumer<TagConsumer<Block>>): TagsProvider<Block> = add { out ->
        object : BlockTagsProvider(out, registries, modID, existingFiles) {
            override fun addTags(registries: HolderLookup.Provider) {
                tags.accept { x -> LibTagAppender(PlatformRegistries.BLOCKS, getOrCreateRawBuilder(x)) }
            }
        }
    }

    override fun entityTags(modID: String, tags: Consumer<TagConsumer<EntityType<*>>>): TagsProvider<EntityType<*>> = add { out ->
        object : EntityTypeTagsProvider(out, registries, modID, existingFiles) {
            override fun addTags(arg: HolderLookup.Provider) {
                tags.accept { x -> LibTagAppender(PlatformRegistries.ENTITY_TYPES, getOrCreateRawBuilder(x)) }
            }
        }
    }

    override fun itemTags(modID: String, tags: Consumer<ItemTagConsumer>, blocks: TagsProvider<Block>): TagsProvider<Item> = add { out ->
        object : ItemTagsProvider(out, registries, blocks.contentsGetter(), modID, existingFiles) {
            fun copyTag(block: TagKey<Block>, item: TagKey<Item>) = copy(block, item)

            override fun addTags(registries: HolderLookup.Provider) {
                val self = this
                tags.accept(object : ItemTagConsumer {
                    override fun tag(tag: TagKey<Item>): LibTagAppender<Item> = LibTagAppender(PlatformRegistries.ITEMS, getOrCreateRawBuilder(tag))

                    override fun copy(
                        block: TagKey<Block>,
                        item: TagKey<Item>,
                    ) {
                        self.copyTag(block, item)
                    }
                })
            }
        }
    }

    override fun models(blocks: Consumer<BlockModelGenerators>, items: Consumer<ItemModelGenerators>) {
        add(DataProvider.Factory { ModelProvider(it, blocks, items) })
    }
}
