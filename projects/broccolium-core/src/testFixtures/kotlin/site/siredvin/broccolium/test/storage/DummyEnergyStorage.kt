package site.siredvin.broccolium.test.storage

import site.siredvin.broccolium.modules.storage.base.api.SomethingOperator
import site.siredvin.broccolium.modules.storage.energy.AgnosticEnergyStack
import site.siredvin.broccolium.modules.storage.energy.EnergyStorageUtils
import site.siredvin.broccolium.modules.storage.energy.api.AgnosticEnergyStorage
import java.util.function.Predicate

class DummyEnergyStorage(val capacity: Long, initialEnergy: AgnosticEnergyStack) : AgnosticEnergyStorage {
    private var internalEnergy: AgnosticEnergyStack = initialEnergy

    override val firstEnergy: AgnosticEnergyStack
        get() = internalEnergy

    override fun getContent(): Iterator<AgnosticEnergyStack> = listOf(firstEnergy).iterator()

    override fun take(predicate: Predicate<AgnosticEnergyStack>, limit: Long, simulate: Boolean): AgnosticEnergyStack {
        if (predicate.test(internalEnergy)) {
            if (simulate) {
                return internalEnergy.copyWithCount(limit.coerceAtMost(internalEnergy.amount))
            }
            return internalEnergy.split(limit)
        }
        return AgnosticEnergyStack(internalEnergy.unit, 0)
    }

    override val canExtract: Boolean
        get() = true

    override fun store(stack: AgnosticEnergyStack, simulate: Boolean): AgnosticEnergyStack {
        if (stack.unit != internalEnergy.unit) return stack
        val possibleInjection = minOf(capacity - internalEnergy.amount, stack.amount)
        if (possibleInjection == 0L) return stack
        if (simulate) {
            stack.shrink(possibleInjection)
            return stack
        }
        internalEnergy.grow(possibleInjection)
        stack.shrink(possibleInjection)
        return stack
    }

    override fun setChanged() {
    }

    override val maxStackSize: Long
        get() = Long.MAX_VALUE
    override val operator: SomethingOperator<AgnosticEnergyStack, Long>
        get() = EnergyStorageUtils

    override val canReceive: Boolean
        get() = true
}
