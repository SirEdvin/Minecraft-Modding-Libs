package site.siredvin.broccolium.modules.platform

import net.minecraft.core.component.DataComponentType
import net.minecraft.resources.ResourceLocation
import net.minecraft.stats.Stat
import net.minecraft.stats.StatFormatter
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeInput
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import site.siredvin.broccolium.modules.base.item.DescriptiveBlockItem
import site.siredvin.broccolium.modules.data.api.ModInformationHolder
import site.siredvin.broccolium.modules.platform.api.InnerBasePlatform
import site.siredvin.broccolium.modules.platform.api.MenuBuilder
import site.siredvin.broccolium.modules.platform.api.RegistryEntry
import java.util.function.Supplier

abstract class BasePlatform {
    abstract val baseInnerPlatform: InnerBasePlatform
    abstract val modInformationTracker: ModInformationTracker

    @Suppress("UNCHECKED_CAST")
    open val holder: ModInformationHolder
        get() = modInformationTracker

    fun <T : Item> registerItem(key: ResourceLocation, item: Supplier<T>): RegistryEntry<T> {
        val registeredItem = SimpleRegistryEntry(key, baseInnerPlatform.registerItem(key, item))
        modInformationTracker.internalItems.add(registeredItem)
        return registeredItem
    }

    fun <T> registerDataComponent(key: ResourceLocation, dataComponent: DataComponentType.Builder<T>): RegistryEntry<DataComponentType<T>> = SimpleRegistryEntry(key, baseInnerPlatform.registerDataComponent(key, dataComponent))

    fun <T : Item> registerItem(name: String, item: Supplier<T>): RegistryEntry<T> = registerItem(ResourceLocation.fromNamespaceAndPath(baseInnerPlatform.modID, name), item)

    fun <T : Block> registerBlock(key: ResourceLocation, block: Supplier<T>, itemFactory: (T) -> (Item)): RegistryEntry<T> = SimpleRegistryEntry(key, baseInnerPlatform.registerBlock(key, block, itemFactory))

    fun <T : Block> registerBlock(name: String, block: Supplier<T>, itemFactory: (T) -> (Item) = { DescriptiveBlockItem(it, Item.Properties()) }): RegistryEntry<T> {
        val id = ResourceLocation.fromNamespaceAndPath(baseInnerPlatform.modID, name)
        val registeredBlock = SimpleRegistryEntry(
            id,
            baseInnerPlatform
                .registerBlock(id, block, itemFactory),
        )
        modInformationTracker.internalBlocks.add(registeredBlock)
        return registeredBlock
    }

    fun <V : BlockEntity, T : BlockEntityType<V>> registerBlockEntity(
        name: String,
        blockEntityTypeSup: Supplier<T>,
    ): RegistryEntry<T> = registerBlockEntity(ResourceLocation.fromNamespaceAndPath(baseInnerPlatform.modID, name), blockEntityTypeSup)

    fun <V : BlockEntity, T : BlockEntityType<V>> registerBlockEntity(
        key: ResourceLocation,
        blockEntityTypeSup: Supplier<T>,
    ): RegistryEntry<T> = SimpleRegistryEntry(key, baseInnerPlatform.registerBlockEntity(key, blockEntityTypeSup))

    fun <M : AbstractContainerMenu> registerMenu(
        name: String,
        builder: MenuBuilder<M>,
    ): RegistryEntry<MenuType<M>> = SimpleRegistryEntry(ResourceLocation.fromNamespaceAndPath(baseInnerPlatform.modID, name), baseInnerPlatform.registerMenu(ResourceLocation.fromNamespaceAndPath(baseInnerPlatform.modID, name), builder))

    fun registerCreativeTab(key: ResourceLocation, tab: CreativeModeTab): RegistryEntry<CreativeModeTab> = SimpleRegistryEntry(key, baseInnerPlatform.registerCreativeTab(key, tab))

    fun registerCustomStat(id: ResourceLocation, formatter: StatFormatter = StatFormatter.DEFAULT): RegistryEntry<Stat<ResourceLocation>> {
        val registered = SimpleRegistryEntry(id, baseInnerPlatform.registerCustomStat(id, formatter))
        modInformationTracker.internalCustomStats.add(registered)
        return registered
    }

    fun <C : RecipeInput, T : Recipe<C>> registerRecipeSerializer(key: ResourceLocation, serializer: RecipeSerializer<T>): RegistryEntry<RecipeSerializer<T>> = SimpleRegistryEntry(key, baseInnerPlatform.registerRecipeSerializer(key, serializer))

    fun <V : Entity, T : EntityType<V>> registerEntity(key: ResourceLocation, entityTypeSup: Supplier<T>): RegistryEntry<T> = SimpleRegistryEntry(key, baseInnerPlatform.registerEntity(key, entityTypeSup))
}
