package site.siredvin.broccolium.modules.storage.energy

import net.minecraftforge.energy.IEnergyStorage
import site.siredvin.broccolium.modules.storage.energy.api.AgnosticEnergyStorage
import java.util.function.Predicate

class AgnosticEnergyHandlerWrapper(private val handler: IEnergyStorage) : AgnosticEnergyStorage {
    override val energy: AgnosticEnergyStack
        get() = AgnosticEnergyStack(Energies.FORGE, handler.energyStored.toLong())
    override val capacity: Long
        get() = handler.maxEnergyStored.toLong()

    override fun takeEnergy(predicate: Predicate<AgnosticEnergyStack>, limit: Long): AgnosticEnergyStack {
        if (!predicate.test(energy)) return AgnosticEnergyStack.EMPTY
        val extractedEnergy = handler.extractEnergy(limit.toInt(), false)
        return AgnosticEnergyStack(Energies.FORGE, extractedEnergy.toLong())
    }

    override fun storeEnergy(stack: AgnosticEnergyStack): AgnosticEnergyStack {
        if (!stack.`is`(Energies.FORGE)) return stack
        val storedEnergy = handler.receiveEnergy(stack.amount.toInt(), false)
        return AgnosticEnergyStack(Energies.FORGE, storedEnergy.toLong())
    }

    override fun setChanged() {
    }
}
