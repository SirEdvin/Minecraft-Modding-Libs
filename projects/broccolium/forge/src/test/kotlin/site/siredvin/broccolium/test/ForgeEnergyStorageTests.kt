package site.siredvin.broccolium.test

import site.siredvin.broccolium.modules.storage.energy.AgnosticEnergyHandlerWrapper
import site.siredvin.broccolium.modules.storage.energy.AgnosticEnergyStack
import site.siredvin.broccolium.modules.storage.energy.Energies
import site.siredvin.broccolium.modules.storage.energy.EnergyUnit
import site.siredvin.broccolium.modules.storage.energy.api.AgnosticEnergyStorage
import site.siredvin.broccolium.test.storage.DummyEnergyStorage

class ForgeEnergyStorageTests : EnergyStorageTests() {

    override val defaultUnits: EnergyUnit
        get() = Energies.FORGE
    override fun createStorage(energy: AgnosticEnergyStack, capacity: Long, secondary: Boolean): AgnosticEnergyStorage {
        if (energy.unit != defaultUnits) {
            return DummyEnergyStorage(capacity, energy)
        }
        val baseStorage = net.minecraftforge.energy.EnergyStorage(capacity.toInt())
        baseStorage.receiveEnergy(energy.amount.toInt(), false)
        return AgnosticEnergyHandlerWrapper(baseStorage)
    }
}

class ForgeDummyEnergyStorageTests : EnergyStorageTests() {

    override val defaultUnits: EnergyUnit
        get() = Energies.FORGE
    override fun createStorage(energy: AgnosticEnergyStack, capacity: Long, secondary: Boolean): AgnosticEnergyStorage {
        if (energy.unit != defaultUnits || secondary) {
            return DummyEnergyStorage(capacity, energy)
        }
        val baseStorage = net.minecraftforge.energy.EnergyStorage(capacity.toInt())
        baseStorage.receiveEnergy(energy.amount.toInt(), false)
        return AgnosticEnergyHandlerWrapper(baseStorage)
    }
}
