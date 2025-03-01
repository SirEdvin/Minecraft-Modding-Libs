package site.siredvin.tweakium.modules.turtle.api

import dan200.computercraft.api.turtle.ITurtleAccess
import dan200.computercraft.api.turtle.TurtleSide
import site.siredvin.peripheralium.api.peripheral.IOwnedPeripheral

fun interface TurtleUpgradePeripheralBuilder<T : IOwnedPeripheral<*>> {
    fun build(turtle: ITurtleAccess, side: TurtleSide): T
}
