package site.siredvin.broccolium.modules.platform

import net.minecraft.advancements.CriterionTrigger
import net.minecraft.core.component.DataComponentType
import net.minecraft.resources.ResourceLocation
import net.minecraft.stats.Stat
import net.minecraft.stats.StatFormatter
import net.minecraft.stats.Stats
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
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension
import net.neoforged.neoforge.registries.DeferredRegister
import site.siredvin.broccolium.modules.platform.api.InnerBasePlatform
import site.siredvin.broccolium.modules.platform.api.MenuBuilder
import java.util.function.Supplier

abstract class ForgeInnerBasePlatform : InnerBasePlatform {
    open val blocksRegistry: DeferredRegister<Block>?
        get() = null
    open val itemsRegistry: DeferredRegister<Item>?
        get() = null
    open val blockEntityTypesRegistry: DeferredRegister<BlockEntityType<*>>?
        get() = null
    open val creativeTabRegistry: DeferredRegister<CreativeModeTab>?
        get() = null
    open val menuTypes: DeferredRegister<MenuType<*>>?
        get() = null
    open val customStats: DeferredRegister<ResourceLocation>?
        get() = null
    open val criterionTriggers: DeferredRegister<CriterionTrigger<*>>?
        get() = null

    open val recipeSerializers: DeferredRegister<RecipeSerializer<*>>?
        get() = null

    open val entityTypesRegistry: DeferredRegister<EntityType<*>>?
        get() = null

    open val dataComponentTypesRegistry: DeferredRegister<DataComponentType<*>>?
        get() = null

    override fun <T : Item> registerItem(key: ResourceLocation, item: Supplier<T>): Supplier<T> = itemsRegistry!!.register(key.path, item)

    override fun <T : Block> registerBlock(
        key: ResourceLocation,
        block: Supplier<T>,
        itemFactory: (T) -> Item,
    ): Supplier<T> {
        val blockRegister = blocksRegistry!!.register(key.path, block)
        itemsRegistry!!.register(key.path, Supplier { itemFactory(blockRegister.get()) })
        return blockRegister
    }

    override fun <V : BlockEntity, T : BlockEntityType<V>> registerBlockEntity(
        key: ResourceLocation,
        blockEntityTypeSup: Supplier<T>,
    ): Supplier<T> = blockEntityTypesRegistry!!.register(key.path, blockEntityTypeSup)

    override fun <M : AbstractContainerMenu> registerMenu(
        key: ResourceLocation,
        builder: MenuBuilder<M>,
    ): Supplier<MenuType<M>> = menuTypes!!.register(
        key.path,
        Supplier { IMenuTypeExtension.create { windowId, inv, data -> builder.build(windowId, inv, data) } },
    )

    override fun registerCreativeTab(key: ResourceLocation, tab: CreativeModeTab): Supplier<CreativeModeTab> = creativeTabRegistry!!.register(key.path, Supplier { tab })

    override fun registerCustomStat(id: ResourceLocation, formatter: StatFormatter): Supplier<Stat<ResourceLocation>> {
        val registeredStat = customStats!!.register(id.path, Supplier { id })
        return Supplier { Stats.CUSTOM.get(registeredStat.get(), formatter) }
    }

    override fun <T : CriterionTrigger<*>> registerCriterionTrigger(key: ResourceLocation, trigger: T): Supplier<T> = criterionTriggers!!.register(key.path, Supplier { trigger })

    override fun <C : RecipeInput, T : Recipe<C>> registerRecipeSerializer(
        key: ResourceLocation,
        serializer: RecipeSerializer<T>,
    ): Supplier<RecipeSerializer<T>> = recipeSerializers!!.register(key.path, Supplier { serializer })

    override fun <V : Entity, T : EntityType<V>> registerEntity(
        key: ResourceLocation,
        entityTypeSup: Supplier<T>,
    ): Supplier<T> = entityTypesRegistry!!.register(key.path, entityTypeSup)

    override fun <T> registerDataComponent(
        key: ResourceLocation,
        dataComponent: DataComponentType.Builder<T>,
    ): Supplier<DataComponentType<T>> = dataComponentTypesRegistry!!.register(key.path, Supplier { dataComponent.build() })
}
