package site.siredvin.broccolium.test.storage

import site.siredvin.broccolium.modules.storage.base.api.SomethingOperator
import site.siredvin.broccolium.modules.storage.fluid.AgnosticFluidStack
import site.siredvin.broccolium.modules.storage.fluid.FluidStorageUtils
import site.siredvin.broccolium.modules.storage.fluid.api.AgnosticFluidStorage
import java.util.function.Predicate

class DummyFluidStorage(private val maxSlots: Int, initialItems: List<AgnosticFluidStack>) : AgnosticFluidStorage {

    companion object {
        const val STACK_LIMIT = 1000.0
    }

    val fluids: MutableList<AgnosticFluidStack> = mutableListOf()

    init {
        if (initialItems.size > maxSlots) {
            throw IllegalArgumentException("Max slots is too low for you?")
        }
        initialItems.forEach {
            fluids.add(it)
        }
        clean()
    }

    fun clean() {
        fluids.removeIf { it.isEmpty }
    }

    override fun getContent(): Iterator<AgnosticFluidStack> = fluids.iterator()

    override fun getCapacities(): List<Double> = fluids.map { STACK_LIMIT }

    override fun take(predicate: Predicate<AgnosticFluidStack>, limit: Double, simulate: Boolean): AgnosticFluidStack {
        var slidingStack = AgnosticFluidStack.EMPTY
        var slidingLimit = limit
        val toRemove = mutableListOf<Int>()
        fluids.forEachIndexed { index, stack ->
            if (slidingLimit > 0) {
                if (!stack.isEmpty && predicate.test(stack)) {
                    if (slidingStack.isEmpty) {
                        val extractedStack = if (simulate) stack.copy() else stack
                        if (extractedStack.amount > limit) {
                            slidingStack = extractedStack.split(limit)
                        } else {
                            slidingStack = extractedStack
                            toRemove.add(index)
                        }
                        slidingLimit = limit.coerceAtMost(maxStackSize) - slidingStack.amount
                    } else if (FluidStorageUtils.canMerge(slidingStack, stack, maxStackSize)) {
                        val originalCount = stack.amount
                        val remainder = FluidStorageUtils.inplaceMerge(slidingStack, if (simulate) stack.copy() else stack, maxStackSize)
                        slidingLimit -= originalCount - remainder.amount
                        if (remainder.isEmpty) {
                            toRemove.add(index)
                        }
                    }
                }
            }
        }
        if (!simulate) {
            toRemove.asReversed().forEach {
                fluids.removeAt(it)
            }
            clean()
        }
        return slidingStack
    }

    override fun store(stack: AgnosticFluidStack, simulate: Boolean): AgnosticFluidStack {
        fluids.forEach {
            if (FluidStorageUtils.canMerge(it, stack, STACK_LIMIT)) {
                FluidStorageUtils.inplaceMerge(if (simulate) it.copy() else it, stack, STACK_LIMIT)
            }
        }
        if (stack.isEmpty) {
            return AgnosticFluidStack.EMPTY
        }
        var addedBySimulation = 0
        while (!stack.isEmpty) {
            if (fluids.size + addedBySimulation < maxSlots) {
                var splitAwayStack = stack.split(stack.amount.coerceAtMost(STACK_LIMIT))
                if (!simulate) {
                    fluids.add(splitAwayStack)
                } else {
                    addedBySimulation += 1
                }
            } else {
                return stack
            }
        }
        return AgnosticFluidStack.EMPTY
    }

    override fun setChanged() {
    }

    override val maxStackSize: Double
        get() = Long.MAX_VALUE.toDouble()
    override val operator: SomethingOperator<AgnosticFluidStack, Double>
        get() = FluidStorageUtils
}
