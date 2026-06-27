package site.siredvin.broccolium.modules.storage.fluid

import site.siredvin.broccolium.modules.storage.base.api.SomethingOperator
import java.util.function.Predicate

@Suppress("MemberVisibilityCanBePrivate")
object FluidStorageUtils : SomethingOperator<AgnosticFluidStack, Double> {
    val ALWAYS = Predicate<AgnosticFluidStack> { true }

    override fun isEmpty(something: AgnosticFluidStack): Boolean = something.isEmpty

    override fun getSize(something: AgnosticFluidStack): Double = something.amount

    override fun canStack(first: AgnosticFluidStack, second: AgnosticFluidStack): Boolean {
        if (!AgnosticFluidStack.isSameFluid(first, second)) {
            return false
        }
        return AgnosticFluidStack.isSameFluidSameTags(first, second)
    }

    override fun canMerge(first: AgnosticFluidStack, second: AgnosticFluidStack, stackLimit: Double?): Boolean {
        if (!canStack(first, second)) {
            return false
        }
        val realStackLimit = if (stackLimit == null) Double.MAX_VALUE else minOf(stackLimit, Double.MAX_VALUE)
        return first.amount < realStackLimit
    }

    /**
     * Merge second item stack into first one and returns remains
     */
    override fun inplaceMerge(first: AgnosticFluidStack, second: AgnosticFluidStack, mergeLimit: Double?): AgnosticFluidStack {
        if (!canMerge(first, second, mergeLimit)) {
            return second
        }
        val mergeSize = minOf(second.amount, (mergeLimit ?: Double.MAX_VALUE) - first.amount)
        first.grow(mergeSize)
        second.shrink(mergeSize)
        if (second.isEmpty) {
            return AgnosticFluidStack.EMPTY
        }
        return second
    }

    override fun getZero(): Double = 0.0

    override fun isZero(value: Double): Boolean = value == 0.0

    override fun biggerThanZero(value: Double): Boolean = value > 0.0

    override fun min(first: Double, second: Double): Double = first.coerceAtMost(second)

    override fun subtract(first: Double, second: Double): Double = first - second

    override fun add(first: Double, second: Double): Double = first + second
}
