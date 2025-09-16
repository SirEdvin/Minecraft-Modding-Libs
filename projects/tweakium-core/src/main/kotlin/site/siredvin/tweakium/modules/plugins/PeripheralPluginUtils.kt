package site.siredvin.tweakium.modules.plugins

import dan200.computercraft.api.lua.LuaException
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import site.siredvin.broccolium.modules.platform.PlatformRegistries
import site.siredvin.tweakium.modules.platform.ComputerPlatformToolkit
import java.util.function.Predicate

object PeripheralPluginUtils {
    object Type {
        const val INVENTORY = "inventory"
        const val FLUID_STORAGE = "fluid_storage"
        const val ITEM_STORAGE = "item_storage"
        const val ENERGY_STORAGE = "energy_storage"
    }

    private object ConditionQueryField {
        const val OR = "or"
        const val AND = "and"
    }

    private object ObjectQueryField {
        const val NAME = "name"
        const val DISPLAY_NAME = "displayName"
        const val TAG = "tag"
    }

    private object ItemQueryField {
        const val NBT = "nbt"
    }

    private object BlockQueryField

    private val ALWAYS_ITEM_STACK_TRUE: Predicate<ItemStack> = Predicate { true }
    private val ALWAYS_BLOCK_STATE_TRUE: Predicate<BlockState> = Predicate { true }

    fun builtItemNamePredicate(name: String): Predicate<ItemStack> {
        val item = PlatformRegistries.ITEMS.get(ResourceLocation(name))
        if (item == Items.AIR) {
            throw LuaException("There is no item $name")
        }
        return Predicate { it.`is`(item) }
    }

    fun builtItemDisplayNamePredicate(displayName: String): Predicate<ItemStack> = Predicate { it.hoverName.string == displayName }

    fun builtItemTagPredicate(tag: String): Predicate<ItemStack> = Predicate { itemStack -> itemStack.tags.anyMatch { it.location.toString() == tag } }

    fun builtNBTPredicate(nbt: String): Predicate<ItemStack> = Predicate {
        nbt == ComputerPlatformToolkit.get().nbtHash(it.tag)
    }

    fun buildOrItemQueryPredicate(something: Any): Predicate<ItemStack> {
        if (something !is Map<*, *>) {
            throw LuaException("Or predicate should contain table with another predicate maps")
        }
        return something.values.filter { it is Map<*, *> }.map { itemQueryToPredicate(it) }.reduce(Predicate<ItemStack>::or)
    }

    fun buildAndItemQueryPredicate(something: Any): Predicate<ItemStack> {
        if (something !is Map<*, *>) {
            throw LuaException("And predicate should contain table with another predicate maps")
        }
        return something.values.filter { it is Map<*, *> }.map { itemQueryToPredicate(it) }.reduce(Predicate<ItemStack>::and)
    }

    fun itemQueryToPredicate(something: Any?): Predicate<ItemStack> {
        if (something == null) {
            return ALWAYS_ITEM_STACK_TRUE
        }
        if (something is String) {
            return builtItemNamePredicate(something)
        } else if (something is Map<*, *>) {
            if (something.contains(ConditionQueryField.OR)) {
                return buildOrItemQueryPredicate(something)
            }
            if (something.contains(ConditionQueryField.AND)) {
                return buildAndItemQueryPredicate(something)
            }
            var aggregatedPredicate = ALWAYS_ITEM_STACK_TRUE
            if (something.contains(ObjectQueryField.NAME)) {
                aggregatedPredicate = aggregatedPredicate.and(builtItemNamePredicate(something[ObjectQueryField.NAME].toString()))
            }
            if (something.contains(ObjectQueryField.DISPLAY_NAME)) {
                aggregatedPredicate = aggregatedPredicate.and(builtItemDisplayNamePredicate(something[ObjectQueryField.DISPLAY_NAME].toString()))
            }
            if (something.contains(ObjectQueryField.TAG)) {
                aggregatedPredicate = aggregatedPredicate.and(builtItemTagPredicate(something[ObjectQueryField.TAG].toString()))
            }
            if (something.contains(ItemQueryField.NBT)) {
                aggregatedPredicate = aggregatedPredicate.and(builtNBTPredicate(something[ItemQueryField.NBT].toString()))
            }
            return aggregatedPredicate
        }
        throw LuaException("Item query should be string or table")
    }

    fun builtBlockNamePredicate(name: String): Predicate<BlockState> {
        val block = PlatformRegistries.BLOCKS.get(ResourceLocation(name))
        if (block == Blocks.AIR) {
            throw LuaException("There is no item $name")
        }
        return Predicate { it.`is`(block) }
    }

    fun builtBlockDisplayNamePredicate(displayName: String): Predicate<BlockState> = Predicate { it.block.descriptionId == displayName }

    fun builtBlockTagPredicate(tag: String): Predicate<BlockState> = Predicate { blockState -> blockState.tags.anyMatch { it.location.toString() == tag } }

    fun buildOrBlockQueryPredicate(something: Any): Predicate<BlockState> {
        if (something !is Map<*, *>) {
            throw LuaException("Or predicate should contain table with another predicate maps")
        }
        return something.values.filter { it is Map<*, *> }.map { blockQueryToPredicate(it) }.reduce(Predicate<BlockState>::or)
    }

    fun buildAndBlockQueryPredicate(something: Any): Predicate<BlockState> {
        if (something !is Map<*, *>) {
            throw LuaException("And predicate should contain table with another predicate maps")
        }
        return something.values.filter { it is Map<*, *> }.map { blockQueryToPredicate(it) }.reduce(Predicate<BlockState>::and)
    }

    fun blockQueryToPredicate(something: Any?): Predicate<BlockState> {
        if (something == null) {
            return ALWAYS_BLOCK_STATE_TRUE
        }
        if (something is String) {
            return builtBlockNamePredicate(something)
        } else if (something is Map<*, *>) {
            if (something.contains(ConditionQueryField.OR)) {
                return buildOrBlockQueryPredicate(something)
            }
            if (something.contains(ConditionQueryField.AND)) {
                return buildAndBlockQueryPredicate(something)
            }
            var aggregatedPredicate = ALWAYS_BLOCK_STATE_TRUE
            if (something.contains(ObjectQueryField.NAME)) {
                aggregatedPredicate = aggregatedPredicate.and(builtBlockNamePredicate(something[ObjectQueryField.NAME].toString()))
            }
            if (something.contains(ObjectQueryField.DISPLAY_NAME)) {
                aggregatedPredicate = aggregatedPredicate.and(builtBlockDisplayNamePredicate(something[ObjectQueryField.DISPLAY_NAME].toString()))
            }
            if (something.contains(ObjectQueryField.TAG)) {
                aggregatedPredicate = aggregatedPredicate.and(builtBlockTagPredicate(something[ObjectQueryField.TAG].toString()))
            }
            return aggregatedPredicate
        }
        throw LuaException("Block query should be string or table")
    }
}
