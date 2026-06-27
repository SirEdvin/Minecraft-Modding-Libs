package site.siredvin.tweakium.modules.minecraft.computercraft

import dan200.computercraft.api.lua.IArguments
import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.api.peripheral.IComputerAccess
import net.minecraft.resources.ResourceLocation
import site.siredvin.broccolium.modules.platform.PlatformRegistries
import site.siredvin.broccolium.modules.storage.energy.AgnosticEnergySinkLookup
import site.siredvin.broccolium.modules.storage.energy.AgnosticEnergyStack
import site.siredvin.broccolium.modules.storage.energy.EnergyRegistry
import site.siredvin.broccolium.modules.storage.fluid.AgnosticFluidSinkLookup
import site.siredvin.broccolium.modules.storage.fluid.AgnosticFluidStack
import site.siredvin.broccolium.modules.storage.item.AgnosticItemSinkLookup
import site.siredvin.tweakium.modules.peripheral.OwnedPeripheral
import site.siredvin.tweakium.modules.peripheral.api.IPeripheralOwner
import site.siredvin.tweakium.modules.peripheral.api.ISidedPeripheral

class CreativeFillerPeripheral(owner: IPeripheralOwner) : OwnedPeripheral<IPeripheralOwner>(TYPE, owner) {
    override val isEnabled: Boolean
        get() = true

    companion object {
        const val TYPE = "creative_filler"
        val STRATEGIES: Map<String, FillerStrategy> = mapOf(
            "item" to ItemFillerStrategy(),
            "fluid" to FluidFillerStrategy(),
            "energy" to EnergyFillerStrategy(),
        )
    }

    abstract class FillerStrategy {
        abstract fun put(access: IComputerAccess, owner: IPeripheralOwner, target: String, id: String, limit: Int)
    }

    class ItemFillerStrategy : FillerStrategy() {
        override fun put(
            access: IComputerAccess,
            owner: IPeripheralOwner,
            target: String,
            id: String,
            limit: Int,
        ) {
            val location =
                access.getAvailablePeripheral(target) ?: throw LuaException("Target '$target' does not exist")
            val direction = if (location is ISidedPeripheral) location.side else null
            val storage = AgnosticItemSinkLookup.extractFromUnknown(owner.level!!, location.target, direction)
                ?: throw LuaException("Source '$target' is not an inventory")
            val item = PlatformRegistries.ITEMS.tryGet(ResourceLocation.parse(id)) ?: throw LuaException("There is no item $id")
            @Suppress("DEPRECATION")
            storage.store(item.defaultInstance.copyWithCount(limit.coerceAtMost(item.defaultMaxStackSize)), false)
        }
    }

    class FluidFillerStrategy : FillerStrategy() {
        override fun put(
            access: IComputerAccess,
            owner: IPeripheralOwner,
            target: String,
            id: String,
            limit: Int,
        ) {
            val location =
                access.getAvailablePeripheral(target) ?: throw LuaException("Target '$target' does not exist")
            val direction = if (location is ISidedPeripheral) location.side else null
            val storage = AgnosticFluidSinkLookup.extractFromUnknown(owner.level!!, location.target, direction)
                ?: throw LuaException("Source '$target' is not an inventory")
            val fluid = PlatformRegistries.FLUIDS.tryGet(ResourceLocation.parse(id)) ?: throw LuaException("There is no fluid $id")
            storage.store(AgnosticFluidStack(fluid, limit.toDouble()), false)
        }
    }

    class EnergyFillerStrategy : FillerStrategy() {
        override fun put(
            access: IComputerAccess,
            owner: IPeripheralOwner,
            target: String,
            id: String,
            limit: Int,
        ) {
            val location =
                access.getAvailablePeripheral(target) ?: throw LuaException("Target '$target' does not exist")
            val direction = if (location is ISidedPeripheral) location.side else null
            val storage = AgnosticEnergySinkLookup.extractFromUnknown(owner.level!!, location.target, direction)
                ?: throw LuaException("Source '$target' is not an inventory")
            val energy = EnergyRegistry.ENERGIES[id] ?: throw LuaException("There is no energy $id")
            storage.store(AgnosticEnergyStack(energy, limit.toLong()), false)
        }
    }

    @LuaFunction(mainThread = true)
    fun put(access: IComputerAccess, arguments: IArguments) {
        val mode = arguments.getString(0)
        val strategy = STRATEGIES[mode] ?: throw LuaException("Invalid mode $mode")
        strategy.put(access, peripheralOwner, arguments.getString(1), arguments.getString(2), arguments.optInt(3, 64))
    }
}
