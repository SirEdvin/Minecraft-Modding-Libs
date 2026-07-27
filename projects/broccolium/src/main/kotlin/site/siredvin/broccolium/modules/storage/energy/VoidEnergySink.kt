package site.siredvin.broccolium.modules.storage.energy

import site.siredvin.broccolium.modules.storage.base.api.SomethingOperator
import site.siredvin.broccolium.modules.storage.energy.api.AgnosticEnergySink

class VoidEnergySink(val unit: EnergyUnit) : AgnosticEnergySink {
    override fun store(
        stack: AgnosticEnergyStack,
        simulate: Boolean,
    ): AgnosticEnergyStack = AgnosticEnergyStack(unit, 0)

    override fun setChanged() {
    }

    override val maxStackSize: Long
        get() = Long.MAX_VALUE
    override val operator: SomethingOperator<AgnosticEnergyStack, Long>
        get() = EnergyStorageUtils

    override val canReceive: Boolean
        get() = true
}
