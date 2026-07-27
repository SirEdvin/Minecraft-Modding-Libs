package site.siredvin.tweakium.modules.plugins

import dan200.computercraft.api.lua.LuaFunction
import site.siredvin.broccolium.modules.storage.energy.api.AgnosticEnergyStorage
import site.siredvin.tweakium.modules.peripheral.api.IPeripheralPlugin

open class EnergyPlugin(protected val storage: AgnosticEnergyStorage) : IPeripheralPlugin {
    override val additionalType: String
        get() = PeripheralPluginUtils.Type.ENERGY_STORAGE

    override fun collectConfiguration(data: MutableMap<String, Any>) {
        data["receiveRate"] = storage.receiveRateLimit
        data["extractRate"] = storage.extractRateLimit
    }

    @LuaFunction(mainThread = true)
    fun getEnergy(): Int = storage.getContent().next().amount.toInt()

    @LuaFunction(mainThread = true)
    fun getEnergyCapacity(): Int = storage.maxStackSize.toInt()

    @LuaFunction(mainThread = true)
    fun getEnergyUnit(): String = storage.getContent().next().unit.name
}
