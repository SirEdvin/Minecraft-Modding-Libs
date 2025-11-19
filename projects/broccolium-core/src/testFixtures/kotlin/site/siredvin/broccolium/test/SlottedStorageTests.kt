package site.siredvin.broccolium.test

import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import site.siredvin.broccolium.modules.storage.base.api.AccessibleAgnosticStorage
import site.siredvin.broccolium.modules.storage.base.api.SlottedAgnosticStorage
import site.siredvin.broccolium.modules.storage.item.ItemStorageUtils
import kotlin.test.assertEquals

abstract class SlottedStorageTests : StorageTests() {

    override fun createStorage(items: List<ItemStack>, secondary: Boolean): AccessibleAgnosticStorage<ItemStack, Int> = createSlottedStorage(items, secondary)
    abstract fun createSlottedStorage(items: List<ItemStack>, secondary: Boolean): SlottedAgnosticStorage<ItemStack, Int>

    fun createSlottedStorage(sizes: List<Int>, stack: ItemStack, secondary: Boolean): SlottedAgnosticStorage<ItemStack, Int> = createSlottedStorage(
        sizes.map {
            if (it == 0) {
                ItemStack.EMPTY
            } else {
                stack.copyWithCount(it)
            }
        },
        secondary,
    )

    data class MoveArguments(
        val initialFrom: List<Int>,
        val initialTo: List<Int>,
        val moveLimit: Int,
        val expectedMoveAmount: Int,
        val expectedFrom: List<Int>,
        val expectedTo: List<Int>,
        val fromSlot: Int = -1,
        val toSlot: Int = -1,
    )

    companion object {
        @JvmStatic
        fun generateMoveSlottedParameters(): List<Arguments> {
            /**
             * Test cases:
             *  - Partial limit move (one stack instead of two)
             *  - Partial limit move (zero stack instead of one)
             *  - Partial limit move (two stacks with two stacks)
             */
            return listOf(
                Arguments.of(
                    MoveArguments(
                        listOf(64, 0, 64),
                        listOf(64, 64, 0),
                        128,
                        64,
                        listOf(0, 0, 64),
                        listOf(64, 64, 64),
                        0,
                        2,
                    ),
                ),
                Arguments.of(
                    MoveArguments(
                        listOf(64, 64, 64),
                        listOf(64, 64, 64),
                        128,
                        0,
                        listOf(64, 64, 64),
                        listOf(64, 64, 64),
                        1,
                        2,
                    ),
                ),
                Arguments.of(
                    MoveArguments(
                        listOf(64, 64, 64),
                        listOf(64, 64, 32),
                        128,
                        32,
                        listOf(32, 64, 64),
                        listOf(64, 64, 64),
                        0,
                        2,
                    ),
                ),
            )
        }
    }

    @Test
    fun testMoveToSameStorage() {
        val dirtBlock = ItemStack(Items.DIRT, 26)
        val storage = createSlottedStorage(listOf(26, 0, 0, 0, 0, 0, 0, 0, 0), dirtBlock, false)
        val movedAmount = storage.moveTo(storage, 64, fromSlot = 0, toSlot = 1, takePredicate = { true })
        assertEquals(26, movedAmount)
        StorageTestHelpers.assertSlottedStorage(storage, listOf(0, 26, 0, 0, 0, 0, 0, 0, 0), "storage")
    }

    @Test
    fun testMoveToSameStorage2() {
        val dirtBlock = ItemStack(Items.DIRT, 26)
        val storage = createSlottedStorage(listOf(0, 26, 0, 0, 0, 0, 0, 0, 0), dirtBlock, false)
        val movedAmount = storage.moveTo(storage, 64, fromSlot = 1, toSlot = 2, takePredicate = { true })
        assertEquals(26, movedAmount)
        StorageTestHelpers.assertSlottedStorage(storage, listOf(0, 0, 26, 0, 0, 0, 0, 0, 0), "storage")
    }

    @ParameterizedTest
    @MethodSource("generateMoveSlottedParameters")
    fun testMoveToSlotted(argument: MoveArguments) {
        val grassBlock = ItemStack(Items.GRASS_BLOCK, 64)
        val from = createSlottedStorage(argument.initialFrom, grassBlock, false)
        val to = createSlottedStorage(argument.initialTo, grassBlock, true)
        val movedAmount = from.moveTo(to, argument.moveLimit, argument.fromSlot, argument.toSlot, takePredicate = ItemStorageUtils.ALWAYS)
        assertEquals(argument.expectedMoveAmount, movedAmount)
        StorageTestHelpers.assertSlottedStorage(from, argument.expectedFrom, "from")
        StorageTestHelpers.assertSlottedStorage(to, argument.expectedTo, "to")
        StorageTestHelpers.assertNoOverlap(from, to)
    }

    @ParameterizedTest
    @MethodSource("generateMoveSlottedParameters")
    fun testMoveFromSlotted(argument: MoveArguments) {
        val grassBlock = ItemStack(Items.GRASS_BLOCK, 64)
        val from = createSlottedStorage(argument.initialFrom, grassBlock, false)
        val to = createSlottedStorage(argument.initialTo, grassBlock, true)
        val movedAmount = to.moveFrom(from, argument.moveLimit, argument.toSlot, argument.fromSlot, takePredicate = ItemStorageUtils.ALWAYS)
        assertEquals(argument.expectedMoveAmount, movedAmount)
        StorageTestHelpers.assertSlottedStorage(from, argument.expectedFrom, "from")
        StorageTestHelpers.assertSlottedStorage(to, argument.expectedTo, "to")
        StorageTestHelpers.assertNoOverlap(from, to)
    }
}
