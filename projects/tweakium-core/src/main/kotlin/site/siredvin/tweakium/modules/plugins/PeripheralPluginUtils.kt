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
        const val INVENTORY_EXTENDED = "inventory_extended"
        const val FLUID_STORAGE = "fluid_storage"
        const val ITEM_STORAGE = "item_storage"
        const val ENERGY_STORAGE = "energy_storage"
        const val ENERGY_STORAGE_EXTENDED = "energy_storage_extended"
        const val FLUID_STORAGE_EXTENDED = "fluid_storage_extended"
    }

    private object ConditionQueryField {
        val OR = setOf(
            "or",
            "or_",
            "owo",
            "any",
        )
        val AND = setOf(
            "and",
            "and_",
            "uwu",
            "all",
        )
        val NOT = setOf(
            "not",
            "no",
            "not_",
            "negate",
            "nawu",
        )
        val NONE = setOf(
            "none",
            "nuwu",
        )
    }

    private object ObjectQueryField {
        val NAME = setOf(
            "name",
            "namae",
        )
        val DISPLAY_NAME = setOf(
            "displayName",
            "tekisuto",
        )
        val TAG = setOf(
            "tag",
            "taguru",
        )
    }

    private object StringQueryField {
        val IN = setOf(
            "in",
            "in_",
            "naibu",
        )
        val NOT_IN = setOf(
            "not_in",
            "wanai",
        )
    }

    private object ItemQueryField {
        val NBT = setOf(
            "nbt",
            "nebeturu",
        )
    }

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

    private fun <T> buildNonePredicate(something: Any?, predicateBuilder: Function<Map<*, *>, Predicate<T>>): Predicate<T> {
        if (something !is Map<*, *>) {
            throw LuaException("And predicate should contain table with another predicate maps")
        }
        return something.values.filter { it is Map<*, *> }.map { predicateBuilder.apply(it as Map<*, *>).negate() }.reduce(Predicate<T>::and)
    }

    private fun <T> buildNotPredicate(something: Any?, predicateBuilder: Function<Map<*, *>, Predicate<T>>): Predicate<T> {
        if (something !is Map<*, *>) {
            throw LuaException("And predicate should contain table with another predicate maps")
        }
        return predicateBuilder.apply(something as Map<*, *>).negate()
    }

    private fun <T> buildValuePredicate(value: Any?, simplePredicate: (value: String) -> Predicate<T>, listPredicate: (value: Set<String>) -> Predicate<T>): Predicate<T> {
        when (value) {
            is String -> {
                return simplePredicate(value)
            }

            is Map<*, *> -> {
                for (entry in value.entries) {
                    if (StringQueryField.IN.contains(entry.key)) {
                        return listPredicate((entry.value as Map<*, *>).values.map { it.toString() }.toSet())
                    } else if (StringQueryField.NOT_IN.contains(entry.key)) {
                        return listPredicate((entry.value as Map<*, *>).values.map { it.toString() }.toSet()).negate()
                    }
                }
                throw LuaException("in or not_in instructions should be in the table")
            }

            else -> {
                throw LuaException("Unsupported argument for filter")
            }
        }
    }

    private fun buildItemNamePredicate(name: String): Predicate<ItemStack> {
        val item = PlatformRegistries.ITEMS.get(ResourceLocation(name))
        if (item == Items.AIR) {
            throw LuaException("There is no item $name")
        }
        return Predicate { it.`is`(item) }
    }

    private fun buildItemNameInPredicate(names: Set<String>): Predicate<ItemStack> {
        val items = names.map { PlatformRegistries.ITEMS.get(ResourceLocation(it)) }.filter { it != Items.AIR }.toSet()
        if (items.isEmpty()) {
            throw LuaException("Zero valid items for filtering by name")
        }
        return Predicate { items.contains(it.item) }
    }

    private fun buildItemDisplayNamePredicate(displayName: String): Predicate<ItemStack> = Predicate { it.hoverName.string == displayName }

    private fun buildItemTagPredicate(tag: String): Predicate<ItemStack> = Predicate { itemStack -> itemStack.tags.anyMatch { it.location.toString() == tag } }

    private fun buildItemTagInPredicate(tags: Set<String>): Predicate<ItemStack> = Predicate { itemStack -> itemStack.tags.anyMatch { tags.contains(it.location.toString()) } }

    private fun buildNBTPredicate(nbt: String): Predicate<ItemStack> = Predicate {
        nbt == ComputerPlatformToolkit.get().nbtHash(it.tag)
    }

    private val ITEM_OPERATION_CONFIGURATION: List<Pair<Set<String>, Function<Any, Predicate<ItemStack>>>> = listOf(
        Pair(ConditionQueryField.OR, Function<Any, Predicate<ItemStack>> { buildOrPredicate(it, ::itemQueryToPredicate) }),
        Pair(ConditionQueryField.AND, Function<Any, Predicate<ItemStack>> { buildAndPredicate(it, ::itemQueryToPredicate) }),
        Pair(ConditionQueryField.NONE, Function<Any, Predicate<ItemStack>> { buildNonePredicate(it, ::itemQueryToPredicate) }),
        Pair(ConditionQueryField.NOT, Function<Any, Predicate<ItemStack>> { buildNotPredicate(it, ::itemQueryToPredicate) }),
        Pair(ObjectQueryField.NAME, Function<Any, Predicate<ItemStack>> { buildValuePredicate(it, ::buildItemNamePredicate, ::buildItemNameInPredicate) }),
        Pair(ObjectQueryField.TAG, Function<Any, Predicate<ItemStack>> { buildValuePredicate(it, ::buildItemTagPredicate, ::buildItemTagInPredicate) }),
        Pair(ObjectQueryField.DISPLAY_NAME, Function<Any, Predicate<ItemStack>> { buildItemDisplayNamePredicate(it.toString()) }),
        Pair(ItemQueryField.NBT, Function<Any, Predicate<ItemStack>> { buildNBTPredicate(it.toString()) }),
    )

    fun itemQueryToPredicate(something: Any?): Predicate<ItemStack> {
        if (something == null) {
            return ALWAYS_ITEM_STACK_TRUE
        }
        if (something is String) {
            return buildItemNamePredicate(something)
        } else if (something is Map<*, *>) {
            var aggregatedPredicate = ALWAYS_ITEM_STACK_TRUE
            for (entry in something.entries) {
                for (instruction in ITEM_OPERATION_CONFIGURATION) {
                    if (instruction.first.contains(entry.key) && entry.value != null) {
                        aggregatedPredicate = aggregatedPredicate.and(
                            instruction.second.apply(entry.value!!),
                        )
                    }
                }
            }
            return aggregatedPredicate
        }
        throw LuaException("Item query should be string or table")
    }

    private fun buildBlockNamePredicate(name: String): Predicate<BlockState> {
        val block = PlatformRegistries.BLOCKS.get(ResourceLocation(name))
        if (block == Blocks.AIR) {
            throw LuaException("There is no item $name")
        }
        return Predicate { it.`is`(block) }
    }

    private fun buildBlockNameInPredicate(names: Set<String>): Predicate<BlockState> {
        val items = names.map { PlatformRegistries.BLOCKS.get(ResourceLocation(it)) }.filter { it != Blocks.AIR }.toSet()
        if (items.isEmpty()) {
            throw LuaException("Zero valid items for filtering by name")
        }
        return Predicate { items.contains(it.block) }
    }

    private fun buildBlockDisplayNamePredicate(displayName: String): Predicate<BlockState> = Predicate { it.block.descriptionId == displayName }

    private fun buildBlockTagPredicate(tag: String): Predicate<BlockState> = Predicate { blockState -> blockState.tags.anyMatch { it.location.toString() == tag } }

    private fun buildBlockTagInPredicate(tags: Set<String>): Predicate<BlockState> = Predicate { blockState -> blockState.tags.anyMatch { tags.contains(it.location.toString()) } }

    private val BLOCK_OPERATION_CONFIGURATION: List<Pair<Set<String>, Function<Any, Predicate<BlockState>>>> = listOf(
        Pair(ConditionQueryField.OR, Function<Any, Predicate<BlockState>> { buildOrPredicate(it, ::blockQueryToPredicate) }),
        Pair(ConditionQueryField.AND, Function<Any, Predicate<BlockState>> { buildAndPredicate(it, ::blockQueryToPredicate) }),
        Pair(ConditionQueryField.NONE, Function<Any, Predicate<BlockState>> { buildNonePredicate(it, ::blockQueryToPredicate) }),
        Pair(ConditionQueryField.NOT, Function<Any, Predicate<BlockState>> { buildNotPredicate(it, ::blockQueryToPredicate) }),
        Pair(ObjectQueryField.NAME, Function<Any, Predicate<BlockState>> { buildValuePredicate(it, ::buildBlockNamePredicate, ::buildBlockNameInPredicate) }),
        Pair(ObjectQueryField.TAG, Function<Any, Predicate<BlockState>> { buildValuePredicate(it, ::buildBlockTagPredicate, ::buildBlockTagInPredicate) }),
        Pair(ObjectQueryField.DISPLAY_NAME, Function<Any, Predicate<BlockState>> { buildBlockDisplayNamePredicate(it.toString()) }),
    )

    fun blockQueryToPredicate(something: Any?): Predicate<BlockState> {
        if (something == null) {
            return ALWAYS_BLOCK_STATE_TRUE
        }
        if (something is String) {
            return buildBlockNamePredicate(something)
        } else if (something is Map<*, *>) {
            var aggregatedPredicate = ALWAYS_BLOCK_STATE_TRUE
            for (entry in something.entries) {
                for (instruction in BLOCK_OPERATION_CONFIGURATION) {
                    if (instruction.first.contains(entry.key) && entry.value != null) {
                        aggregatedPredicate = aggregatedPredicate.and(
                            instruction.second.apply(entry.value!!),
                        )
                    }
                }
            }
            return aggregatedPredicate
        }
        throw LuaException("Block query should be string or table")
    }
}
