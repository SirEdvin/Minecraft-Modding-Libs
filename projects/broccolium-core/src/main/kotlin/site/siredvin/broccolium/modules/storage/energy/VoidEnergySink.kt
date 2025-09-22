package site.siredvin.broccolium.modules.storage.energy

import site.siredvin.broccolium.modules.storage.energy.api.AgnosticEnergySink

class VoidEnergySink(override val unit: EnergyUnit) : AgnosticEnergySink {
    override fun storeEnergy(stack: AgnosticEnergyStack): AgnosticEnergyStack = AgnosticEnergyStack(unit, 0)

    override fun setChanged() {
    }

    override val canReceive: Boolean
        get() = true
}
