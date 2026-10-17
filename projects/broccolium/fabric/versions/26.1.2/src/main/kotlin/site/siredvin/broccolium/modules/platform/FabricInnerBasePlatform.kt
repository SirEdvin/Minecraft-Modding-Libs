package site.siredvin.broccolium.modules.platform

import net.fabricmc.fabric.api.menu.v1.ExtendedMenuType
import net.minecraft.advancements.CriterionTrigger
import net.minecraft.core.Registry
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier
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
import site.siredvin.broccolium.modules.platform.api.InnerBasePlatform
import site.siredvin.broccolium.modules.platform.api.MenuBuilder
import java.util.function.Supplier

abstract class FabricInnerBasePlatform : InnerBasePlatform {
    override fun <T : Item> registerItem(key: Identifier, item: Supplier<T>): Supplier<T> {
        val registered = Registry.register(BuiltInRegistries.ITEM, key, item.get())
        return Supplier { registered }
    }

    override fun <T : Block> registerBlock(key: Identifier, block: Supplier<T>, itemFactory: (T) -> Item): Supplier<T> {
        val registered = Registry.register(BuiltInRegistries.BLOCK, key, block.get())
        Registry.register(BuiltInRegistries.ITEM, key, itemFactory(registered))
        return Supplier { registered }
    }

    override fun <V : BlockEntity, T : BlockEntityType<V>> registerBlockEntity(key: Identifier, blockEntityTypeSup: Supplier<T>): Supplier<T> {
        val registered = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, key, blockEntityTypeSup.get())
        return Supplier { registered }
    }

    override fun <M : AbstractContainerMenu> registerMenu(key: Identifier, builder: MenuBuilder<M>): Supplier<MenuType<M>> {
        val menu = ExtendedMenuType({ id, inventory, data -> FabricMenuOpeningData.build(builder, id, inventory, data) }, FabricMenuOpeningData.codec)
        val registered = Registry.register(BuiltInRegistries.MENU, key, menu)
        return Supplier { registered }
    }

    override fun registerCreativeTab(key: Identifier, tab: CreativeModeTab): Supplier<CreativeModeTab> {
        val registered = Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, key, tab)
        return Supplier { registered }
    }

    override fun registerCustomStat(id: Identifier, formatter: StatFormatter): Supplier<Stat<Identifier>> {
        val registered = Registry.register(BuiltInRegistries.CUSTOM_STAT, id, id)
        return Supplier { Stats.CUSTOM.get(registered, formatter) }
    }

    override fun <T : CriterionTrigger<*>> registerCriterionTrigger(key: Identifier, trigger: T): Supplier<T> {
        val registered = Registry.register(BuiltInRegistries.TRIGGER_TYPES, key, trigger)
        return Supplier { registered }
    }

    override fun <C : RecipeInput, T : Recipe<C>> registerRecipeSerializer(key: Identifier, serializer: RecipeSerializer<T>): Supplier<RecipeSerializer<T>> {
        val registered = Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, key, serializer)
        return Supplier { registered }
    }

    override fun <V : Entity, T : EntityType<V>> registerEntity(key: Identifier, entityTypeSup: Supplier<T>): Supplier<T> {
        val registered = Registry.register(BuiltInRegistries.ENTITY_TYPE, key, entityTypeSup.get())
        return Supplier { registered }
    }

    override fun <T : Any> registerDataComponent(key: Identifier, dataComponent: DataComponentType.Builder<T>): Supplier<DataComponentType<T>> {
        val registered = Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, key, dataComponent.build())
        return Supplier { registered }
    }
}
