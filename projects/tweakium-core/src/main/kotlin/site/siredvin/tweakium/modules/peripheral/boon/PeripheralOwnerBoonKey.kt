package site.siredvin.tweakium.modules.peripheral.boon

import site.siredvin.tweakium.modules.peripheral.api.IPeripheralOwnerBoon
import site.siredvin.tweakium.modules.peripheral.api.IPeripheralOwnerBoonKey

class PeripheralOwnerBoonKey<T : IPeripheralOwnerBoon> : IPeripheralOwnerBoonKey<T> {
    companion object {
        val FUEL: PeripheralOwnerBoonKey<FuelBoon<*>> = PeripheralOwnerBoonKey()
        val OPERATION: PeripheralOwnerBoonKey<OperationBoon> = PeripheralOwnerBoonKey()
        val EXPERIENCE: PeripheralOwnerBoonKey<ExperienceBoon> = PeripheralOwnerBoonKey()
        val SCANNING: PeripheralOwnerBoonKey<ScanningBoon<*>> = PeripheralOwnerBoonKey()
    }
}
