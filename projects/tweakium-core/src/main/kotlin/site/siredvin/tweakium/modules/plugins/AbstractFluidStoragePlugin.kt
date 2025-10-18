package site.siredvin.tweakium.modules.plugins

import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.api.peripheral.IComputerAccess
import dan200.computercraft.api.peripheral.IPeripheral
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.Level
import net.minecraft.world.level.material.Fluids
import site.siredvin.broccolium.modules.platform.PlatformRegistries
import site.siredvin.broccolium.modules.platform.PlatformToolkit
import site.siredvin.broccolium.modules.storage.fluid.AgnosticFluidStack
import site.siredvin.broccolium.modules.storage.fluid.AgnosticFluidStorageLookup
import site.siredvin.broccolium.modules.storage.fluid.api.AgnosticFluidStorage
import site.siredvin.tweakium.modules.peripheral.api.IPeripheralPlugin
import site.siredvin.tweakium.modules.peripheral.representation.LuaRepresentation
import java.util.*
import java.util.function.Predicate

abstract class AbstractFluidStoragePlugin(protected val level: Level, protected val fluidStorageTransferLimit: Double) : IPeripheralPlugin {
    override val additionalTypes: List<String>
        get() = listOf(
            PeripheralPluginUtils.Type.FLUID_STORAGE,
            PeripheralPluginUtils.Type.FLUID_STORAGE_EXTENDED,
        )

    protected open fun fluidInformation(fluid: AgnosticFluidStack): MutableMap<String, Any?> = LuaRepresentation.forFluidStack(fluid)

    protected abstract val storage: AgnosticFluidStorage

    override fun collectConfiguration(data: MutableMap<String, Any>) {
        data["fluidStorageTransferLimit"] = fluidStorageTransferLimit / PlatformToolkit.get().fluidCompactDivider
        data["fluidStorageAPIVersion"] = listOf(1, 2)
        data["platformCompactDivider"] = PlatformToolkit.get().fluidCompactDivider
        data["WhoBlameForAPIDesign"] = listOf("SirEdvin", "Wojbie")
    }

    @LuaFunction(mainThread = true)
    fun tanks(): List<Map<String, *>> {
        val data: MutableList<Map<String, *>> = mutableListOf()
        storage.getFluids().forEach {
            data.add(fluidInformation(it))
        }
        return data
    }

    @LuaFunction(mainThread = true)
    fun capacities(): List<Double> = storage.getCapacities()

    @LuaFunction(mainThread = true)
    fun pushFluid(computer: IComputerAccess, toName: String, limit: Optional<Double>, fluidName: Optional<String>): Double {
        val location: IPeripheral = computer.getAvailablePeripheral(toName)
            ?: throw LuaException("Target '$toName' does not exist")

        val toStorage = AgnosticFluidStorageLookup.extractFluidSinkFromUnknown(level, location.target)
            ?: throw LuaException("Target '$toName' is not an fluid storage")

        val predicate: Predicate<AgnosticFluidStack> = if (fluidName.isEmpty) {
            Predicate { true }
        } else {
            val fluid = PlatformRegistries.FLUIDS.get(ResourceLocation(fluidName.get()))
            if (fluid.isSame(Fluids.EMPTY)) {
                throw LuaException("There is no fluid ${fluidName.get()}")
            }
            Predicate { it.fluid.isSame(fluid) }
        }
        val realLimit = minOf(fluidStorageTransferLimit, limit.orElse(Double.MAX_VALUE))
        return storage.moveTo(toStorage, realLimit, predicate)
    }

    @LuaFunction(mainThread = true)
    fun pullFluid(computer: IComputerAccess, fromName: String, limit: Optional<Double>, fluidName: Optional<String>): Double {
        val location: IPeripheral = computer.getAvailablePeripheral(fromName)
            ?: throw LuaException("Target '$fromName' does not exist")

        val fromStorage = AgnosticFluidStorageLookup.extractFluidStorageFromUnknown(level, location.target)
            ?: throw LuaException("Target '$fromName' is not an fluid storage")

        val predicate: Predicate<AgnosticFluidStack> = if (fluidName.isEmpty) {
            Predicate { true }
        } else {
            val fluid = PlatformRegistries.FLUIDS.get(ResourceLocation(fluidName.get()))
            if (fluid.isSame(Fluids.EMPTY)) {
                throw LuaException("There is no fluid ${fluidName.get()}")
            }
            Predicate { it.fluid.isSame(fluid) }
        }
        val realLimit = minOf(fluidStorageTransferLimit, limit.orElse(Double.MAX_VALUE))
        return storage.moveFrom(fromStorage, realLimit, predicate)
    }
}
