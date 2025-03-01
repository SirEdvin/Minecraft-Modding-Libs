package site.siredvin.broccolium.test.storage

import site.siredvin.broccolium.modules.storage.energy.AgnosticEnergyStack
import site.siredvin.broccolium.modules.storage.energy.Energies
import site.siredvin.broccolium.modules.storage.energy.api.AgnosticEnergyStorage
import java.util.function.Predicate

class DummyEnergyStorage(override val capacity: Long, initialEnergy: AgnosticEnergyStack) : AgnosticEnergyStorage {
    private var internalEnergy: AgnosticEnergyStack = initialEnergy

    override val energy: AgnosticEnergyStack
        get() = internalEnergy

    override fun takeEnergy(predicate: Predicate<AgnosticEnergyStack>, limit: Long): AgnosticEnergyStack {
        if (predicate.test(internalEnergy)) return internalEnergy.split(limit)
        return AgnosticEnergyStack.EMPTY
    }

    override fun storeEnergy(stack: AgnosticEnergyStack): AgnosticEnergyStack {
        if (stack.unit != internalEnergy.unit && internalEnergy.unit != Energies.EMPTY) return stack
        val possibleInjection = minOf(capacity - internalEnergy.amount, stack.amount)
        if (possibleInjection == 0L) return stack
        if (internalEnergy.unit == Energies.EMPTY) {
            internalEnergy = AgnosticEnergyStack(stack.unit, possibleInjection)
        } else {
            internalEnergy.grow(possibleInjection)
        }
        stack.shrink(possibleInjection)
        return stack
    }

    override fun setChanged() {
    }
}
