package site.siredvin.tweakium.modules.plugins

import dan200.computercraft.api.lua.IArguments
import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.api.peripheral.IComputerAccess
import net.minecraft.world.level.Level
import site.siredvin.broccolium.modules.storage.energy.AgnosticEnergySinkLookup
import site.siredvin.broccolium.modules.storage.energy.AgnosticEnergyStorageLookup
import site.siredvin.broccolium.modules.storage.energy.api.AgnosticEnergyStorage
import site.siredvin.tweakium.modules.peripheral.api.ISidedPeripheral
import java.util.Optional
import kotlin.math.floor

open class FullEnergyPlugin(protected val level: Level, storage: AgnosticEnergyStorage, private val energyStorageTransferLimit: Int) : EnergyPlugin(storage) {
    override val additionalTypes: List<String>
        get() = listOf(PeripheralPluginUtils.Type.ENERGY_STORAGE, PeripheralPluginUtils.Type.ENERGY_STORAGE_EXTENDED)

    private fun checkedLimit(arguments: IArguments): Optional<Long> {
        if (arguments.count() <= 1 || arguments.get(1) == null) return Optional.empty()
        val value = arguments.getDouble(1)
        if (!value.isFinite() || value < Long.MIN_VALUE.toDouble() || value >= Long.MAX_VALUE.toDouble() || floor(value) != value) throw LuaException("Energy limit must be an integer in the signed 64-bit range")
        return Optional.of(value.toLong())
    }

    @LuaFunction(mainThread = true, value = ["pushEnergy"])
    fun pushEnergyLua(computer: IComputerAccess, arguments: IArguments): Long = pushEnergy(computer, arguments.getString(0), checkedLimit(arguments))

    @LuaFunction(mainThread = true, value = ["pullEnergy"])
    fun pullEnergyLua(computer: IComputerAccess, arguments: IArguments): Double = pullEnergy(computer, arguments.getString(0), checkedLimit(arguments))

    fun pushEnergy(computer: IComputerAccess, toName: String, limit: Optional<Long>): Long {
        val location = computer.getAvailablePeripheral(toName) ?: throw LuaException("Target '$toName' does not exist")
        val direction = if (location is ISidedPeripheral) location.side else null
        val toStorage = AgnosticEnergySinkLookup.extractFromUnknown(level, location.target, direction) ?: throw LuaException("Target '$toName' is not an energy storage")
        return storage.moveTo(toStorage, minOf(energyStorageTransferLimit.toLong(), limit.orElse(Long.MAX_VALUE)), -1, { true })
    }

    fun pullEnergy(computer: IComputerAccess, fromName: String, limit: Optional<Long>): Double {
        val location = computer.getAvailablePeripheral(fromName) ?: throw LuaException("Target '$fromName' does not exist")
        val direction = if (location is ISidedPeripheral) location.side else null
        val fromStorage = AgnosticEnergyStorageLookup.extractFromUnknown(level, location.target, direction) ?: throw LuaException("Target '$fromName' is not an energy storage")
        return storage.moveFrom(fromStorage, minOf(energyStorageTransferLimit.toLong(), limit.orElse(Long.MAX_VALUE)), -1, { true }).toDouble()
    }
}
