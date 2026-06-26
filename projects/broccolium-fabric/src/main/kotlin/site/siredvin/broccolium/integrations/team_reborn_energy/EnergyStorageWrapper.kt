package site.siredvin.broccolium.integrations.team_reborn_energy

import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction
import site.siredvin.broccolium.modules.storage.base.api.SomethingOperator
import site.siredvin.broccolium.modules.storage.energy.AgnosticEnergyStack
import site.siredvin.broccolium.modules.storage.energy.Energies
import site.siredvin.broccolium.modules.storage.energy.EnergyStorageUtils
import site.siredvin.broccolium.modules.storage.energy.api.AgnosticEnergyStorage
import team.reborn.energy.api.EnergyStorage
import java.util.function.Predicate

class EnergyStorageWrapper(private val energyStorage: EnergyStorage) : AgnosticEnergyStorage {

    override val maxStackSize: Long
        get() = energyStorage.capacity
    override val operator: SomethingOperator<AgnosticEnergyStack, Long>
        get() = EnergyStorageUtils
    override val canExtract: Boolean
        get() = energyStorage.supportsExtraction()
    override val firstEnergy: AgnosticEnergyStack
        get() = AgnosticEnergyStack(Energies.REDSTONE_FLUX, energyStorage.amount)

    override fun setChanged() {
    }

    override val canReceive: Boolean
        get() = energyStorage.supportsInsertion()

    override fun store(stack: AgnosticEnergyStack, simulate: Boolean): AgnosticEnergyStack {
        if (stack.unit != Energies.REDSTONE_FLUX) return stack
        Transaction.openOuter().use {
            val stored = energyStorage.insert(stack.amount, it)
            if (!simulate) {
                it.commit()
            } else {
                it.abort()
            }
            stack.shrink(stored)
            return stack
        }
    }

    override fun take(predicate: Predicate<AgnosticEnergyStack>, limit: Long, simulate: Boolean): AgnosticEnergyStack {
        if (!predicate.test(firstEnergy)) return AgnosticEnergyStack(Energies.REDSTONE_FLUX, 0)
        Transaction.openOuter().use {
            val extractedAmount = energyStorage.extract(limit, it)
            if (!simulate) {
                it.commit()
            } else {
                it.abort()
            }
            return AgnosticEnergyStack(Energies.REDSTONE_FLUX, extractedAmount)
        }
    }

    override fun getContent(): Iterator<AgnosticEnergyStack> = listOf(firstEnergy).iterator()
}
