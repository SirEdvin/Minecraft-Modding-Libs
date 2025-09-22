package site.siredvin.tweakium.modules.plugins

import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.api.peripheral.IComputerAccess
import dan200.computercraft.api.peripheral.IPeripheral
import net.minecraft.world.level.Level
import site.siredvin.broccolium.modules.storage.energy.AgnosticEnergyStorageLookup
import site.siredvin.broccolium.modules.storage.energy.api.AgnosticEnergyStorage
import java.util.*

open class FullEnergyPlugin(protected val level: Level, storage: AgnosticEnergyStorage, private val energyStorageTransferLimit: Int) : EnergyPlugin(storage) {
    override val additionalType: String
        get() = PeripheralPluginUtils.Type.ENERGY_STORAGE

    @LuaFunction(mainThread = true)
    fun pushEnergy(computer: IComputerAccess, toName: String, limit: Optional<Long>): Long {
        val location: IPeripheral = computer.getAvailablePeripheral(toName)
            ?: throw LuaException("Target '$toName' does not exist")

        val toStorage = AgnosticEnergyStorageLookup.extractEnergySinkFromUnknown(level, location.target)
            ?: throw LuaException("Target '$toName' is not an energy storage")

        val realLimit = minOf(energyStorageTransferLimit.toLong(), limit.orElse(Long.MAX_VALUE))
        return storage.moveTo(toStorage, realLimit, { true })
    }

    @LuaFunction(mainThread = true)
    fun pullEnergy(computer: IComputerAccess, fromName: String, limit: Optional<Long>): Double {
        val location: IPeripheral = computer.getAvailablePeripheral(fromName)
            ?: throw LuaException("Target '$fromName' does not exist")

        val fromStorage = AgnosticEnergyStorageLookup.extractEnergyStorageFromUnknown(level, location.target)
            ?: throw LuaException("Target '$fromName' is not an energy storage")

        val realLimit = minOf(energyStorageTransferLimit.toLong(), limit.orElse(Long.MAX_VALUE))
        return storage.moveFrom(fromStorage, realLimit, { true }).toDouble()
    }
}
