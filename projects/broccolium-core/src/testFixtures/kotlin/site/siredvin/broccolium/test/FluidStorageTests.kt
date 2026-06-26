package site.siredvin.broccolium.test

import net.minecraft.world.level.material.Fluids
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import site.siredvin.broccolium.modules.storage.fluid.AgnosticFluidStack
import site.siredvin.broccolium.modules.storage.fluid.FluidStorageUtils
import site.siredvin.broccolium.modules.storage.fluid.api.AgnosticFluidStorage
import java.util.function.Predicate
import kotlin.math.min

abstract class FluidStorageTests {
    abstract fun createStorage(fluids: List<AgnosticFluidStack>, secondary: Boolean): AgnosticFluidStorage

    fun createStorage(sizes: List<Double>, stack: AgnosticFluidStack, secondary: Boolean): AgnosticFluidStorage = createStorage(
        sizes.map {
            if (it == 0.0) {
                AgnosticFluidStack.EMPTY
            } else {
                stack.copyWithCount(it)
            }
        },
        secondary,
    )

    data class MoveArguments(
        val initialFrom: List<Double>,
        val initialTo: List<Double>,
        val moveLimit: Double,
        val expectedMoveAmount: Double,
        val expectedFrom: List<Double>,
        val expectedTo: List<Double>,
    )

    data class TakeArgument(
        val firstIndices: List<Double>,
        val resultIndices: List<Double>,
        val limit: Double,
    )

    data class StoreArgument(
        val firstIndices: List<Double>,
        val resultIndices: List<Double>,
        val stackSize: Double,
        val reminderSize: Double,
    )

    companion object {
        @JvmStatic
        fun generateTakeTestParameters(): List<Arguments> = listOf(
            Arguments.of(
                TakeArgument(listOf(1000.0, 1000.0, 0.0), listOf(0.0, 0.0, 0.0), 2000.0),
            ),
            Arguments.of(
                TakeArgument(listOf(1000.0, 1000.0, 0.0), listOf(1000.0, 500.0, 0.0), 500.0),
            ),
        )

        @JvmStatic
        fun generateStoreTestParameters(): List<Arguments> = listOf(
            Arguments.of(
                StoreArgument(listOf(1000.0, 1000.0, 0.0, 0.0), listOf(1000.0, 1000.0, 1000.0, 0.0), 1000.0, 0.0),
            ),
            Arguments.of(
                StoreArgument(listOf(1000.0, 1000.0, 0.0, 0.0), listOf(1000.0, 1000.0, 500.0, 0.0), 500.0, 0.0),
            ),
            Arguments.of(
                StoreArgument(listOf(1000.0, 1000.0, 500.0), listOf(1000.0, 1000.0, 1000.0), 500.0, 0.0),
            ),
            Arguments.of(
                StoreArgument(listOf(1000.0, 1000.0, 500.0), listOf(1000.0, 1000.0, 1000.0), 1000.0, 500.0),
            ),
            Arguments.of(
                StoreArgument(listOf(1000.0, 1000.0, 1000.0), listOf(1000.0, 1000.0, 1000.0), 500.0, 500.0),
            ),
            Arguments.of(
                StoreArgument(listOf(1000.0, 1000.0, 1000.0), listOf(1000.0, 1000.0, 1000.0), 1000.0, 1000.0),
            ),
        )

        @JvmStatic
        fun generateMoveToParameters(): List<Arguments> {
            /**
             * Test cases:
             *  - Partial limit move (one stack instead of two)
             *  - Partial limit move (zero stack instead of one)
             *  - Partial limit move (two stacks with two stacks)
             */
            return listOf(
                Arguments.of(
                    MoveArguments(
                        listOf(1000.0, 1000.0, 1000.0),
                        listOf(1000.0, 1000.0, 0.0),
                        1000.0,
                        1000.0,
                        listOf(1000.0, 1000.0),
                        listOf(1000.0, 1000.0, 1000.0),
                    ),
                ),
                Arguments.of(
                    MoveArguments(
                        listOf(1000.0, 1000.0, 1000.0),
                        listOf(1000.0, 1000.0, 1000.0),
                        2000.0,
                        0.0,
                        listOf(1000.0, 1000.0, 1000.0),
                        listOf(1000.0, 1000.0, 1000.0),
                    ),
                ),
                Arguments.of(
                    MoveArguments(
                        listOf(1000.0, 1000.0, 1000.0),
                        listOf(1000.0, 1000.0, 500.0),
                        2000.0,
                        500.0,
                        listOf(500.0, 1000.0, 1000.0),
                        listOf(1000.0, 1000.0, 1000.0),
                    ),
                ),
            )
        }
    }

    @ParameterizedTest
    @MethodSource("generateTakeTestParameters")
    fun testSimulation(argument: TakeArgument) {
        val grassBlock = AgnosticFluidStack(Fluids.LAVA, 1000.0)
        val first = createStorage(argument.firstIndices, grassBlock, secondary = false)
        val second = createStorage(argument.firstIndices, grassBlock, secondary = true)
        val firstStack = first.take(FluidStorageUtils.ALWAYS, argument.limit, true)
        assertEquals(min(first.maxStackSize, argument.limit), firstStack.amount, "Stack doesn't match?")
        StorageTestHelpers.assertStorage(first, argument.firstIndices, "Storage suppose to be unchanged")
        val secondStack = second.take(FluidStorageUtils.ALWAYS, argument.limit, true)
        assertEquals(min(second.maxStackSize, argument.limit), secondStack.amount, "Stack doesn't match?")
        StorageTestHelpers.assertStorage(second, argument.firstIndices, "Storage suppose to be unchanged")
    }

    @ParameterizedTest
    @MethodSource("generateTakeTestParameters")
    fun testAction(argument: TakeArgument) {
        val grassBlock = AgnosticFluidStack(Fluids.LAVA, 1000.0)
        val first = createStorage(argument.firstIndices, grassBlock, secondary = false)
        val second = createStorage(argument.firstIndices, grassBlock, secondary = true)
        val firstStack = first.take(FluidStorageUtils.ALWAYS, argument.limit, false)
        assertEquals(min(first.maxStackSize, argument.limit), firstStack.amount, "Stack doesn't match?")
        StorageTestHelpers.assertStorage(first, argument.resultIndices, "Storage suppose to be changed")
        val secondStack = second.take(FluidStorageUtils.ALWAYS, argument.limit, false)
        assertEquals(min(second.maxStackSize, argument.limit), secondStack.amount, "Stack doesn't match?")
        StorageTestHelpers.assertStorage(second, argument.resultIndices, "Storage suppose to be changed")
    }

    @ParameterizedTest
    @MethodSource("generateStoreTestParameters")
    fun testStoreSimulation(argument: StoreArgument) {
        val grassBlock = AgnosticFluidStack(Fluids.LAVA, 1000.0)
        val first = createStorage(argument.firstIndices, grassBlock, secondary = false)
        val second = createStorage(argument.firstIndices, grassBlock, secondary = true)
        val stackToStore = AgnosticFluidStack(Fluids.LAVA, argument.stackSize)
        val firstStack = first.store(stackToStore.copy(), true)
        assertEquals(argument.reminderSize, firstStack.amount, "Stack doesn't match?")
        StorageTestHelpers.assertStorage(first, argument.firstIndices, "Storage suppose to be unchanged")
        val secondStack = second.store(stackToStore.copy(), true)
        assertEquals(argument.reminderSize, secondStack.amount, "Stack doesn't match?")
        StorageTestHelpers.assertStorage(second, argument.firstIndices, "Storage suppose to be unchanged")
    }

    @ParameterizedTest
    @MethodSource("generateStoreTestParameters")
    fun testStoreAction(argument: StoreArgument) {
        val grassBlock = AgnosticFluidStack(Fluids.LAVA, 1000.0)
        val first = createStorage(argument.firstIndices, grassBlock, secondary = false)
        val second = createStorage(argument.firstIndices, grassBlock, secondary = true)
        val stackToStore = AgnosticFluidStack(Fluids.LAVA, argument.stackSize)
        val firstStack = first.store(stackToStore.copy(), false)
        assertEquals(argument.reminderSize, firstStack.amount, "Stack doesn't match?")
        StorageTestHelpers.assertStorage(first, argument.resultIndices, "Storage suppose to be unchanged")
        val secondStack = second.store(stackToStore.copy(), false)
        assertEquals(argument.reminderSize, secondStack.amount, "Stack doesn't match?")
        StorageTestHelpers.assertStorage(second, argument.resultIndices, "Storage suppose to be unchanged")
    }

    @ParameterizedTest
    @MethodSource("generateMoveToParameters")
    fun testMoveTo(argument: MoveArguments) {
        val water = AgnosticFluidStack(Fluids.WATER, 1000.0)
        val from = createStorage(argument.initialFrom, water, secondary = false)
        val to = createStorage(argument.initialTo, water, secondary = true)
        val movedAmount = from.moveTo(to, argument.moveLimit, takePredicate = FluidStorageUtils.ALWAYS)
        assertEquals(argument.expectedMoveAmount, movedAmount, "count")
        StorageTestHelpers.assertStorage(from, argument.expectedFrom, "from")
        StorageTestHelpers.assertStorage(to, argument.expectedTo, "to")
        StorageTestHelpers.assertNoOverlap(from, to)
    }

    @ParameterizedTest
    @MethodSource("generateMoveToParameters")
    fun testMoveFrom(argument: MoveArguments) {
        val water = AgnosticFluidStack(Fluids.WATER, 1000.0)
        val from = createStorage(argument.initialFrom, water, secondary = false)
        val to = createStorage(argument.initialTo, water, secondary = true)
        val movedAmount = to.moveFrom(from, argument.moveLimit, takePredicate = FluidStorageUtils.ALWAYS)
        assertEquals(argument.expectedMoveAmount, movedAmount, "count")
        StorageTestHelpers.assertStorage(from, argument.expectedFrom, "from")
        StorageTestHelpers.assertStorage(to, argument.expectedTo, "to")
        StorageTestHelpers.assertNoOverlap(from, to)
    }

    @Test
    fun testPredicateSearch() {
        val from = createStorage(
            listOf(
                AgnosticFluidStack(Fluids.WATER, 1000.0),
                AgnosticFluidStack(Fluids.LAVA, 1.0),
                AgnosticFluidStack(Fluids.WATER, 500.0),
            ),
            true,
        )
        val to = createStorage(listOf(100.0, 0.0, 0.0), AgnosticFluidStack(Fluids.WATER, 1000.0), true)
        val predicate: Predicate<AgnosticFluidStack> = Predicate {
            it.fluid.isSame(Fluids.LAVA)
        }
        val movedAmount = from.moveTo(to, 1.0, takePredicate = predicate)
        assertEquals(1.0, movedAmount, "count")
        StorageTestHelpers.assertStorage(from, listOf(1000.0, 500.0), "from")
        StorageTestHelpers.assertStorage(to, listOf(100.0, 1.0), "to")
        StorageTestHelpers.assertNoOverlap(from, to)
    }

    @Test
    fun testFailedPredicateSearch() {
        val from = createStorage(
            listOf(
                AgnosticFluidStack(Fluids.WATER, 500.0),
                AgnosticFluidStack(Fluids.WATER, 500.0),
                AgnosticFluidStack(Fluids.WATER, 500.0),
            ),
            true,
        )
        val to = createStorage(listOf(1000.0, 0.0, 0.0), AgnosticFluidStack(Fluids.WATER, 1000.0), true)
        val predicate: Predicate<AgnosticFluidStack> = Predicate {
            it.fluid.isSame(Fluids.LAVA)
        }
        val movedAmount = from.moveTo(to, 1.0, takePredicate = predicate)
        assertEquals(0.0, movedAmount, "count")
        StorageTestHelpers.assertStorage(from, listOf(500.0, 500.0, 500.0), "from")
        StorageTestHelpers.assertStorage(to, listOf(1000.0), "to")
        StorageTestHelpers.assertNoOverlap(from, to)
    }
}
