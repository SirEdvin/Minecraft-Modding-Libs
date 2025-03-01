package site.siredvin.tweakium.modules.peripheral.api

import net.minecraft.core.Direction

interface IPeripheralProvider<T : site.siredvin.tweakium.modules.peripheral.api.IOwnedPeripheral<*>> {
    fun getPeripheral(side: Direction): T?
}
