package site.siredvin.broccolium.modules.storage.energy

import net.minecraftforge.energy.IEnergyStorage
import site.siredvin.broccolium.modules.storage.base.api.SomethingOperator
import site.siredvin.broccolium.modules.storage.energy.api.AgnosticEnergyStorage
import java.util.function.Predicate

class AgnosticEnergyHandlerWrapper(private val handler: IEnergyStorage) : AgnosticEnergyStorage {
    override fun getContent(): Iterator<AgnosticEnergyStack> = listOf(firstEnergy).iterator()
    override val firstEnergy: AgnosticEnergyStack
        get() = AgnosticEnergyStack(Energies.FORGE, handler.energyStored.toLong())

    override fun take(predicate: Predicate<AgnosticEnergyStack>, limit: Long, simulate: Boolean): AgnosticEnergyStack {
        if (!predicate.test(firstEnergy)) return AgnosticEnergyStack(Energies.FORGE, 0)
        val extractedEnergy = handler.extractEnergy(limit.toInt(), simulate)
        return AgnosticEnergyStack(Energies.FORGE, extractedEnergy.toLong())
    }

    override val canExtract: Boolean
        get() = handler.canExtract()

    override val canReceive: Boolean
        get() = handler.canReceive()
    override val maxStackSize: Long
        get() = handler.maxEnergyStored.toLong()
    override val operator: SomethingOperator<AgnosticEnergyStack, Long>
        get() = EnergyStorageUtils

    override fun store(stack: AgnosticEnergyStack, simulate: Boolean): AgnosticEnergyStack {
        if (!stack.`is`(Energies.FORGE)) return stack
        val storedEnergy = handler.receiveEnergy(stack.amount.toInt(), simulate)
        return AgnosticEnergyStack(Energies.FORGE, stack.amount - storedEnergy)
    }

    override fun setChanged() {
    }
}
