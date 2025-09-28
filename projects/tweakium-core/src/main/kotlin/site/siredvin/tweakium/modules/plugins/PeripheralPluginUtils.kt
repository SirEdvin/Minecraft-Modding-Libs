package site.siredvin.tweakium.modules.plugins

import dan200.computercraft.api.lua.LuaException
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import site.siredvin.broccolium.modules.platform.PlatformRegistries
import site.siredvin.tweakium.modules.platform.ComputerPlatformToolkit
import java.util.function.Function
import java.util.function.Predicate

object PeripheralPluginUtils {
    object Type {
        const val INVENTORY_VIEW = "inventory_view"
        const val INVENTORY = "inventory"
        const val FLUID_STORAGE = "fluid_storage"
        const val ITEM_STORAGE = "item_storage"
        const val ENERGY_STORAGE = "energy_storage"
    }

    private object ConditionQueryField {
        const val OR = "or"
        const val AND = "and"
        const val NOT = "not"
    }

    private object ObjectQueryField {
        const val NAME = "name"
        const val DISPLAY_NAME = "displayName"
        const val TAG = "tag"
    }

    private object StringQueryField {
        const val IN = "in"
        const val NOT_IN = "not_in"
    }

    private object ItemQueryField {
        const val NBT = "nbt"
    }

    private object BlockQueryField

    private val ALWAYS_ITEM_STACK_TRUE: Predicate<ItemStack> = Predicate { true }
    private val ALWAYS_BLOCK_STATE_TRUE: Predicate<BlockState> = Predicate { true }

    private fun <T> buildOrPredicate(something: Any?, predicateBuilder: Function<Map<*, *>, Predicate<T>>): Predicate<T> {
        if (something !is Map<*, *>) {
            throw LuaException("Or predicate should contain table with another predicate maps")
        }
        val predicates = something.values.filter { it is Map<*, *> }.map { predicateBuilder.apply(it as Map<*, *>) }
        return predicates.reduce(Predicate<T>::or)
    }

    private fun <T> buildAndPredicate(something: Any?, predicateBuilder: Function<Map<*, *>, Predicate<T>>): Predicate<T> {
        if (something !is Map<*, *>) {
            throw LuaException("And predicate should contain table with another predicate maps")
        }
        return something.values.filter { it is Map<*, *> }.map { predicateBuilder.apply(it as Map<*, *>) }.reduce(Predicate<T>::and)
    }

    private fun <T> buildNotPredicate(something: Any?, predicateBuilder: Function<Map<*, *>, Predicate<T>>): Predicate<T> {
        if (something !is Map<*, *>) {
            throw LuaException("And predicate should contain table with another predicate maps")
        }
        return predicateBuilder.apply(something as Map<*, *>).negate()
    }

    private fun builtItemNamePredicate(name: String): Predicate<ItemStack> {
        val item = PlatformRegistries.ITEMS.get(ResourceLocation(name))
        if (item == Items.AIR) {
            throw LuaException("There is no item $name")
        }
        return Predicate { it.`is`(item) }
    }

    private fun builtItemNameInPredicate(names: Set<String>): Predicate<ItemStack> {
        val items = names.map { PlatformRegistries.ITEMS.get(ResourceLocation(it)) }.filter { it != Items.AIR }.toSet()
        if (items.isEmpty()) {
            throw LuaException("Zero valid items for filtering by name")
        }
        return Predicate { items.contains(it.item) }
    }

    private fun builtItemDisplayNamePredicate(displayName: String): Predicate<ItemStack> = Predicate { it.hoverName.string == displayName }

    private fun builtItemTagPredicate(tag: String): Predicate<ItemStack> = Predicate { itemStack -> itemStack.tags.anyMatch { it.location.toString() == tag } }

    private fun builtNBTPredicate(nbt: String): Predicate<ItemStack> = Predicate {
        nbt == ComputerPlatformToolkit.get().nbtHash(it.tag)
    }

    fun itemQueryToPredicate(something: Any?): Predicate<ItemStack> {
        if (something == null) {
            return ALWAYS_ITEM_STACK_TRUE
        }
        if (something is String) {
            return builtItemNamePredicate(something)
        } else if (something is Map<*, *>) {
            if (something.contains(ConditionQueryField.OR)) {
                return buildOrPredicate(something[ConditionQueryField.OR], ::itemQueryToPredicate)
            }
            if (something.contains(ConditionQueryField.AND)) {
                return buildAndPredicate(something[ConditionQueryField.AND], ::itemQueryToPredicate)
            }
            if (something.contains(ConditionQueryField.NOT)) {
                return buildNotPredicate(something[ConditionQueryField.NOT], ::itemQueryToPredicate)
            }
            var aggregatedPredicate = ALWAYS_ITEM_STACK_TRUE
            if (something.contains(ObjectQueryField.NAME)) {
                val condition = something[ObjectQueryField.NAME]
                if (condition is String) {
                    aggregatedPredicate = aggregatedPredicate.and(builtItemNamePredicate(condition))
                } else if (condition is Map<*, *>) {
                    if (condition.contains(StringQueryField.IN)) {
                        aggregatedPredicate = aggregatedPredicate.and(builtItemNameInPredicate((condition[StringQueryField.IN] as Map<*, *>).values.map { it.toString() }.toSet()))
                    } else if (condition.contains(StringQueryField.NOT_IN)) {
                        aggregatedPredicate = aggregatedPredicate.and(builtItemNameInPredicate((condition[StringQueryField.NOT_IN] as Map<*, *>).values.map { it.toString() }.toSet()).negate())
                    } else {
                        throw LuaException("Unsupported argument for name filter")
                    }
                } else {
                    throw LuaException("Unsupported argument for name filter")
                }
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

    private fun builtBlockNamePredicate(name: String): Predicate<BlockState> {
        val block = PlatformRegistries.BLOCKS.get(ResourceLocation(name))
        if (block == Blocks.AIR) {
            throw LuaException("There is no item $name")
        }
        return Predicate { it.`is`(block) }
    }

    private fun builtBlockNameInPredicate(names: Set<String>): Predicate<BlockState> {
        val items = names.map { PlatformRegistries.BLOCKS.get(ResourceLocation(it)) }.filter { it != Blocks.AIR }.toSet()
        if (items.isEmpty()) {
            throw LuaException("Zero valid items for filtering by name")
        }
        return Predicate { items.contains(it.block) }
    }

    private fun builtBlockDisplayNamePredicate(displayName: String): Predicate<BlockState> = Predicate { it.block.descriptionId == displayName }

    private fun builtBlockTagPredicate(tag: String): Predicate<BlockState> = Predicate { blockState -> blockState.tags.anyMatch { it.location.toString() == tag } }

    fun blockQueryToPredicate(something: Any?): Predicate<BlockState> {
        if (something == null) {
            return ALWAYS_BLOCK_STATE_TRUE
        }
        if (something is String) {
            return builtBlockNamePredicate(something)
        } else if (something is Map<*, *>) {
            if (something.contains(ConditionQueryField.OR)) {
                return buildOrPredicate(something[ConditionQueryField.OR], ::blockQueryToPredicate)
            }
            if (something.contains(ConditionQueryField.AND)) {
                return buildAndPredicate(something[ConditionQueryField.AND], ::blockQueryToPredicate)
            }
            if (something.contains(ConditionQueryField.NOT)) {
                return buildNotPredicate(something[ConditionQueryField.NOT], ::blockQueryToPredicate)
            }
            var aggregatedPredicate = ALWAYS_BLOCK_STATE_TRUE
            if (something.contains(ObjectQueryField.NAME)) {
                val condition = something[ObjectQueryField.NAME]
                if (condition is String) {
                    aggregatedPredicate = aggregatedPredicate.and(builtBlockNamePredicate(condition))
                } else if (condition is Map<*, *>) {
                    if (condition.contains(StringQueryField.IN)) {
                        aggregatedPredicate = aggregatedPredicate.and(builtBlockNameInPredicate((condition[StringQueryField.IN] as Map<*, *>).values.map { it.toString() }.toSet()))
                    } else if (condition.contains(StringQueryField.NOT_IN)) {
                        aggregatedPredicate = aggregatedPredicate.and(builtBlockNameInPredicate((condition[StringQueryField.NOT_IN] as Map<*, *>).values.map { it.toString() }.toSet()).negate())
                    } else {
                        throw LuaException("Unsupported argument for name filter")
                    }
                } else {
                    throw LuaException("Unsupported argument for name filter")
                }
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
