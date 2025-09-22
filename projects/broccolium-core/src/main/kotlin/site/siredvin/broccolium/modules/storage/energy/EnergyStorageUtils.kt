package site.siredvin.broccolium.modules.storage.energy

import site.siredvin.broccolium.modules.storage.energy.api.AgnosticEnergySink
import site.siredvin.broccolium.modules.storage.energy.api.AgnosticEnergyStorage
import java.util.function.Predicate

@Suppress("MemberVisibilityCanBePrivate")
object EnergyStorageUtils {

    val ALWAYS: Predicate<AgnosticEnergyStack> = Predicate { true }

    fun naiveMove(from: AgnosticEnergyStorage, to: AgnosticEnergySink, limit: Long, takePredicate: Predicate<AgnosticEnergyStack>): Long {
        val conversionMode = from.unit != to.unit
        if (conversionMode && !EnergyRegistry.isConvertible(from.unit, to.unit)) {
            return 0
        }
        // Get stack to move
        var stack = from.takeEnergy(takePredicate, limit)
        if (stack.isEmpty) {
            return 0
        }

        val stackCount = stack.amount
        if (conversionMode) {
            stack = EnergyRegistry.convert(stack, to.unit)
        }

        // Move item to
        var remainder = to.storeEnergy(stack)
        if (conversionMode) {
            remainder = EnergyRegistry.convert(remainder, from.unit, true)
        }

        // Calculate items moved
        val count = stackCount - remainder.amount
        if (!remainder.isEmpty) {
            // Put reminder back
            from.storeEnergy(remainder)
        }
        return count
    }

    fun canStack(first: AgnosticEnergyStack, second: AgnosticEnergyStack): Boolean = AgnosticEnergyStack.isSameEnergy(first, second)

    fun canMerge(first: AgnosticEnergyStack, second: AgnosticEnergyStack, stackLimit: Long = -1): Boolean {
        if (!canStack(first, second)) {
            return false
        }
        val realStackLimit = if (stackLimit == -1L) Long.MAX_VALUE else minOf(stackLimit, Long.MAX_VALUE)
        return first.amount < realStackLimit
    }

    /**
     * Merge second item stack into first one and returns remains
     */
    fun inplaceMerge(first: AgnosticEnergyStack, second: AgnosticEnergyStack, mergeLimit: Long = Long.MAX_VALUE): AgnosticEnergyStack {
        if (!canMerge(first, second, mergeLimit)) {
            return second
        }
        val mergeSize = minOf(second.amount, mergeLimit - first.amount)
        first.grow(mergeSize)
        second.shrink(mergeSize)
        return second
    }
}
