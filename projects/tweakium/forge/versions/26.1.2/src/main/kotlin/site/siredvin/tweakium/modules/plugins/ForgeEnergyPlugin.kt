package site.siredvin.tweakium.modules.plugins

import dan200.computercraft.api.lua.LuaFunction
import net.neoforged.neoforge.transfer.energy.EnergyHandler
import site.siredvin.tweakium.modules.peripheral.api.IPeripheralPlugin

class ForgeEnergyPlugin(private val storage: EnergyHandler) : IPeripheralPlugin {
    override val additionalType: String
        get() = PeripheralPluginUtils.Type.ENERGY_STORAGE

    @LuaFunction(mainThread = true)
    fun getEnergy(): Long = storage.amountAsLong

    @LuaFunction(mainThread = true)
    fun getEnergyCapacity(): Long = storage.capacityAsLong

    @LuaFunction(mainThread = true)
    fun getEnergyUnit(): String = "FE"
}
