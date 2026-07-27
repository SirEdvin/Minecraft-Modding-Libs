package site.siredvin.tweakium.modules.peripheral.api

import net.minecraft.core.Direction

interface IPeripheralProvider<T : IOwnedPeripheral<*>> {
    fun getPeripheral(side: Direction): T?
}
