package site.siredvin.tweakium.modules.peripheral

import site.siredvin.tweakium.modules.peripheral.owner.DisabledPeripheralOwner

object DisabledPeripheral : OwnedPeripheral<DisabledPeripheralOwner>("disabled", DisabledPeripheralOwner()) {
    override val isEnabled: Boolean
        get() = true

    override fun equals(other: Any?): Boolean = other === this
}
