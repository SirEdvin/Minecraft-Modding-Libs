package site.siredvin.broccolium.modules.storage.energy

import site.siredvin.broccolium.modules.storage.base.api.SomethingOperator
import java.util.function.Predicate

@Suppress("MemberVisibilityCanBePrivate")
object EnergyStorageUtils : SomethingOperator<AgnosticEnergyStack, Long> {
    val ALWAYS = Predicate<AgnosticEnergyStack> { true }
    override fun isEmpty(something: AgnosticEnergyStack): Boolean = something.isEmpty

    override fun getSize(something: AgnosticEnergyStack): Long = something.amount

    override fun canStack(first: AgnosticEnergyStack, second: AgnosticEnergyStack): Boolean = AgnosticEnergyStack.isSameEnergy(first, second)

    override fun canMerge(first: AgnosticEnergyStack, second: AgnosticEnergyStack, stackLimit: Long?): Boolean {
        if (!canStack(first, second)) {
            return false
        }
        val realStackLimit = if (stackLimit == null) Long.MAX_VALUE else minOf(stackLimit, Long.MAX_VALUE)
        return first.amount < realStackLimit
    }

    /**
     * Merge second item stack into first one and returns remains
     */
    override fun inplaceMerge(first: AgnosticEnergyStack, second: AgnosticEnergyStack, mergeLimit: Long?): AgnosticEnergyStack {
        if (!canMerge(first, second, mergeLimit)) {
            return second
        }
        val mergeSize = minOf(second.amount, (mergeLimit ?: Long.MAX_VALUE) - first.amount)
        first.grow(mergeSize)
        second.shrink(mergeSize)
        return second
    }

    override fun getZero(): Long = 0L

    override fun isZero(value: Long): Boolean = value == 0L

    override fun biggerThanZero(value: Long): Boolean = value > 0

    override fun min(first: Long, second: Long): Long = first.coerceAtMost(second)

    override fun subtract(first: Long, second: Long): Long = first - second

    override fun add(first: Long, second: Long): Long = first + second
}
