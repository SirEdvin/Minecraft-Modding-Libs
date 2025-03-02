package site.siredvin.tweakium.modules.plugins

import dan200.computercraft.api.lua.LuaFunction
import site.siredvin.broccolium.modules.storage.energy.api.AgnosticEnergyStorage
import site.siredvin.tweakium.modules.peripheral.api.IPeripheralPlugin

class EnergyPlugin(private val storage: AgnosticEnergyStorage) : IPeripheralPlugin {
    override val additionalType: String
        get() = PeripheralPluginUtils.Type.ENERGY_STORAGE

    @LuaFunction(mainThread = true)
    fun getEnergy(): Int = storage.energy.amount.toInt()

    @LuaFunction(mainThread = true)
    fun getEnergyCapacity(): Int = storage.capacity.toInt()

    @LuaFunction(mainThread = true)
    fun getEnergyUnit(): String = storage.energy.unit.name
}
