package site.siredvin.tweakium.modules.peripheral.api

import dan200.computercraft.api.peripheral.IComputerAccess
import dan200.computercraft.api.peripheral.IPeripheral

interface IOwnedPeripheral<T : site.siredvin.tweakium.modules.peripheral.api.IPeripheralOwner?> : IPeripheral {
    val isEnabled: Boolean
    val connectedComputers: List<IComputerAccess>
    val peripheralOwner: T
}
