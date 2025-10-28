package site.siredvin.broccolium.test.storage

import site.siredvin.broccolium.modules.storage.energy.AgnosticEnergyStack
import site.siredvin.broccolium.modules.storage.energy.EnergyUnit
import site.siredvin.broccolium.modules.storage.energy.api.AgnosticEnergyStorage
import java.util.function.Predicate

class DummyEnergyStorage(override val capacity: Long, initialEnergy: AgnosticEnergyStack) : AgnosticEnergyStorage {
    private var internalEnergy: AgnosticEnergyStack = initialEnergy

    override val energy: AgnosticEnergyStack
        get() = internalEnergy

    override fun takeEnergy(predicate: Predicate<AgnosticEnergyStack>, limit: Long): AgnosticEnergyStack {
        if (predicate.test(internalEnergy)) return internalEnergy.split(limit)
        return AgnosticEnergyStack(internalEnergy.unit, 0)
    }

    override val canExtract: Boolean
        get() = true

    override fun storeEnergy(stack: AgnosticEnergyStack): AgnosticEnergyStack {
        if (stack.unit != internalEnergy.unit) return stack
        val possibleInjection = minOf(capacity - internalEnergy.amount, stack.amount)
        if (possibleInjection == 0L) return stack
        internalEnergy.grow(possibleInjection)
        stack.shrink(possibleInjection)
        return stack
    }

    override fun setChanged() {
    }

    override val canReceive: Boolean
        get() = true
    override val unit: EnergyUnit
        get() = internalEnergy.unit
}
