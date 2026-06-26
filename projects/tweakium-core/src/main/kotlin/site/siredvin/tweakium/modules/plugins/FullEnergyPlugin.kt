package site.siredvin.tweakium.modules.plugins

import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.api.peripheral.IComputerAccess
import dan200.computercraft.api.peripheral.IPeripheral
import net.minecraft.world.level.Level
import site.siredvin.broccolium.modules.storage.energy.AgnosticEnergySinkLookup
import site.siredvin.broccolium.modules.storage.energy.AgnosticEnergyStorageLookup
import site.siredvin.broccolium.modules.storage.energy.api.AgnosticEnergyStorage
import site.siredvin.tweakium.modules.peripheral.api.ISidedPeripheral
import java.util.*

open class FullEnergyPlugin(protected val level: Level, storage: AgnosticEnergyStorage, private val energyStorageTransferLimit: Int) : EnergyPlugin(storage) {

    override val additionalTypes: List<String>
        get() = listOf(PeripheralPluginUtils.Type.ENERGY_STORAGE, PeripheralPluginUtils.Type.ENERGY_STORAGE_EXTENDED)

    @LuaFunction(mainThread = true)
    fun pushEnergy(computer: IComputerAccess, toName: String, limit: Optional<Long>): Long {
        val location: IPeripheral = computer.getAvailablePeripheral(toName)
            ?: throw LuaException("Target '$toName' does not exist")

        val direction = if (location is ISidedPeripheral) location.side else null

        val toStorage = AgnosticEnergySinkLookup.extractFromUnknown(level, location.target, direction)
            ?: throw LuaException("Target '$toName' is not an energy storage")

        val realLimit = minOf(energyStorageTransferLimit.toLong(), limit.orElse(Long.MAX_VALUE))
        return storage.moveTo(toStorage, realLimit, -1, { true })
    }

    @LuaFunction(mainThread = true)
    fun pullEnergy(computer: IComputerAccess, fromName: String, limit: Optional<Long>): Double {
        val location: IPeripheral = computer.getAvailablePeripheral(fromName)
            ?: throw LuaException("Target '$fromName' does not exist")

        val direction = if (location is ISidedPeripheral) location.side else null

        val fromStorage = AgnosticEnergyStorageLookup.extractFromUnknown(level, location.target, direction)
            ?: throw LuaException("Target '$fromName' is not an energy storage")

        val realLimit = minOf(energyStorageTransferLimit.toLong(), limit.orElse(Long.MAX_VALUE))
        return storage.moveFrom(fromStorage, realLimit, -1, { true }).toDouble()
    }
}
