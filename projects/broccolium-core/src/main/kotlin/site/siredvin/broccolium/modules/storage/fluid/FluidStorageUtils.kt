package site.siredvin.broccolium.modules.storage.fluid

import site.siredvin.broccolium.modules.storage.fluid.api.AgnosticFluidSink
import site.siredvin.broccolium.modules.storage.fluid.api.AgnosticFluidStorage
import java.util.function.Predicate

@Suppress("MemberVisibilityCanBePrivate")
object FluidStorageUtils {

    val ALWAYS: Predicate<AgnosticFluidStack> = Predicate { true }

    fun naiveMove(from: AgnosticFluidStorage, to: AgnosticFluidSink, limit: Double, takePredicate: Predicate<AgnosticFluidStack>): Double {
        // Get stack to move
        val stack = from.takeFluid(takePredicate, limit)
        if (stack.isEmpty) {
            return 0.0
        }

        val stackCount = stack.amount

        // Move item to
        val remainder = to.storeFluid(stack)

        // Calculate items moved
        val count = stackCount - remainder.amount
        if (!remainder.isEmpty) {
            // Put reminder back
            from.storeFluid(remainder)
        }
        return count
    }

    fun canStack(first: AgnosticFluidStack, second: AgnosticFluidStack): Boolean {
        if (!AgnosticFluidStack.isSameFluid(first, second)) {
            return false
        }
        return AgnosticFluidStack.isSameFluidSameTags(first, second)
    }

    fun canMerge(first: AgnosticFluidStack, second: AgnosticFluidStack, stackLimit: Long = -1): Boolean {
        if (!canStack(first, second)) {
            return false
        }
        val realStackLimit = if (stackLimit == -1L) Long.MAX_VALUE else minOf(stackLimit, Long.MAX_VALUE)
        return first.amount < realStackLimit
    }

    /**
     * Merge second item stack into first one and returns remains
     */
    fun inplaceMerge(first: AgnosticFluidStack, second: AgnosticFluidStack, mergeLimit: Long = Long.MAX_VALUE): AgnosticFluidStack {
        if (!canMerge(first, second, mergeLimit)) {
            return second
        }
        val mergeSize = minOf(second.amount, mergeLimit - first.amount)
        first.grow(mergeSize)
        second.shrink(mergeSize)
        if (second.isEmpty) {
            return AgnosticFluidStack.EMPTY
        }
        return second
    }
}
