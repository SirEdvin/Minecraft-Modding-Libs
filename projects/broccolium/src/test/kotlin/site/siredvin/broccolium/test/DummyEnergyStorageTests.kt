package site.siredvin.broccolium.test

import site.siredvin.broccolium.modules.storage.energy.AgnosticEnergyStack
import site.siredvin.broccolium.modules.storage.energy.api.AgnosticEnergyStorage
import site.siredvin.broccolium.test.storage.DummyEnergyStorage

@WithMinecraft
class DummyEnergyStorageTests : EnergyStorageTests() {
    override fun createStorage(energy: AgnosticEnergyStack, capacity: Long, secondary: Boolean): AgnosticEnergyStorage = DummyEnergyStorage(capacity, energy)
}
