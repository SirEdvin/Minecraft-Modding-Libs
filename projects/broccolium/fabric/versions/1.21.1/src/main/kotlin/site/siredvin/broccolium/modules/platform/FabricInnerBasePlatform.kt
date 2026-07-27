package site.siredvin.broccolium.modules.platform

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType
import net.minecraft.advancements.CriterionTrigger
import net.minecraft.core.Registry
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
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
import site.siredvin.broccolium.modules.platform.api.InnerBasePlatform
import site.siredvin.broccolium.modules.platform.api.MenuBuilder
import java.util.function.Supplier

abstract class FabricInnerBasePlatform : InnerBasePlatform {
    companion object {
        private val MENU_OPENING_DATA_CODEC: StreamCodec<RegistryFriendlyByteBuf, FriendlyByteBuf> = object : StreamCodec<RegistryFriendlyByteBuf, FriendlyByteBuf> {
            override fun decode(buffer: RegistryFriendlyByteBuf): FriendlyByteBuf = FriendlyByteBuf(buffer.readBytes(buffer.readableBytes()))

            override fun encode(buffer: RegistryFriendlyByteBuf, value: FriendlyByteBuf) {
                buffer.writeBytes(value)
            }
        }
    }

    override fun <T : Item> registerItem(key: ResourceLocation, item: Supplier<T>): Supplier<T> {
        val registeredItem = Registry.register(BuiltInRegistries.ITEM, key, item.get())
        return Supplier { registeredItem }
    }

    override fun <T : Block> registerBlock(
        key: ResourceLocation,
        block: Supplier<T>,
        itemFactory: (T) -> Item,
    ): Supplier<T> {
        val registeredBlock = Registry.register(BuiltInRegistries.BLOCK, key, block.get())
        Registry.register(BuiltInRegistries.ITEM, key, itemFactory(registeredBlock))
        return Supplier { registeredBlock }
    }

    override fun <V : BlockEntity, T : BlockEntityType<V>> registerBlockEntity(
        key: ResourceLocation,
        blockEntityTypeSup: Supplier<T>,
    ): Supplier<T> {
        val registeredBlockEntityType = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, key, blockEntityTypeSup.get())
        return Supplier { registeredBlockEntityType }
    }

    override fun <M : AbstractContainerMenu> registerMenu(
        key: ResourceLocation,
        builder: MenuBuilder<M>,
    ): Supplier<MenuType<M>> {
        val menu = ExtendedScreenHandlerType({ id, inventory, data -> builder.build(id, inventory, data) }, MENU_OPENING_DATA_CODEC)
        return Supplier { Registry.register(BuiltInRegistries.MENU, key, menu) }
    }

    override fun registerCreativeTab(key: ResourceLocation, tab: CreativeModeTab): Supplier<CreativeModeTab> {
        val registeredTab = Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, key, tab)
        return Supplier { registeredTab }
    }

    override fun registerCustomStat(id: ResourceLocation, formatter: StatFormatter): Supplier<Stat<ResourceLocation>> {
        val registeredStat = Registry.register(BuiltInRegistries.CUSTOM_STAT, id, id)
        return Supplier { Stats.CUSTOM.get(registeredStat, formatter) }
    }

    override fun <T : CriterionTrigger<*>> registerCriterionTrigger(key: ResourceLocation, trigger: T): Supplier<T> {
        val registered = Registry.register(BuiltInRegistries.TRIGGER_TYPES, key, trigger)
        return Supplier { registered }
    }

    override fun <C : RecipeInput, T : Recipe<C>> registerRecipeSerializer(
        key: ResourceLocation,
        serializer: RecipeSerializer<T>,
    ): Supplier<RecipeSerializer<T>> {
        val registeredRecipe = Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, key, serializer)
        return Supplier { registeredRecipe }
    }

    override fun <V : Entity, T : EntityType<V>> registerEntity(
        key: ResourceLocation,
        entityTypeSup: Supplier<T>,
    ): Supplier<T> {
        val registeredEntityType = Registry.register(BuiltInRegistries.ENTITY_TYPE, key, entityTypeSup.get())
        return Supplier { registeredEntityType }
    }

    override fun <T> registerDataComponent(
        key: ResourceLocation,
        dataComponent: DataComponentType.Builder<T>,
    ): Supplier<DataComponentType<T>> {
        val registered = Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, key, dataComponent.build())
        return Supplier { registered }
    }
}
