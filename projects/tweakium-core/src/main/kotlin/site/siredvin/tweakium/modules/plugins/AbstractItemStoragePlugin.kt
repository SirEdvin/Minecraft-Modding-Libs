package site.siredvin.tweakium.modules.plugins

import dan200.computercraft.api.lua.IArguments
import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.api.peripheral.IComputerAccess
import dan200.computercraft.api.peripheral.IPeripheral
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import site.siredvin.broccolium.modules.storage.item.AgnosticItemStorageLookup
import site.siredvin.broccolium.modules.storage.item.api.AgnosticItemStorage
import site.siredvin.tweakium.modules.peripheral.api.IPeripheralPlugin
import site.siredvin.tweakium.modules.peripheral.representation.LuaRepresentation
import site.siredvin.tweakium.modules.peripheral.representation.RepresentationMode
import java.util.*
import java.util.function.Predicate
import kotlin.math.min

abstract class AbstractItemStoragePlugin : IPeripheralPlugin {
    abstract val storage: AgnosticItemStorage
    abstract val level: Level
    abstract val itemStorageTransferLimit: Int

    override val additionalType: String
        get() = PeripheralPluginUtils.Type.ITEM_STORAGE

    open fun itemsImpl(mode: RepresentationMode = RepresentationMode.DETAILED): List<MutableMap<String, *>> {
        val result: MutableList<MutableMap<String, *>> = mutableListOf()
        storage.getItems().forEach {
            if (!it.isEmpty) {
                result.add(LuaRepresentation.forItemStack(it, mode))
            }
        }
        return result
    }

    @LuaFunction(mainThread = true)
    fun items(arguments: IArguments): List<Map<String, *>> {
        val isDetailed = arguments.optBoolean(0, true)
        return itemsImpl(mode = if (isDetailed) RepresentationMode.DETAILED else RepresentationMode.BASE)
    }

    @LuaFunction(mainThread = true)
    fun pushItem(computer: IComputerAccess, toName: String, itemQuery: Any?, limit: Optional<Int>): Int {
        val location: IPeripheral = computer.getAvailablePeripheral(toName)
            ?: throw LuaException("Target '$toName' does not exist")

        val toStorage = AgnosticItemStorageLookup.extractItemSinkFromUnknown(level, location.target)
            ?: throw LuaException("Target '$toName' is not an targetable storage")

        val predicate: Predicate<ItemStack> = PeripheralPluginUtils.itemQueryToPredicate(itemQuery)
        val realLimit = min(itemStorageTransferLimit, limit.orElse(Int.MAX_VALUE))
        return storage.moveTo(toStorage, realLimit, takePredicate = predicate)
    }

    @LuaFunction(mainThread = true)
    fun pullItem(computer: IComputerAccess, fromName: String, itemQuery: Any?, limit: Optional<Int>): Int {
        val location: IPeripheral = computer.getAvailablePeripheral(fromName)
            ?: throw LuaException("Target '$fromName' does not exist")

        val fromStorage = AgnosticItemStorageLookup.extractStorageFromUnknown(level, location.target)
            ?: throw LuaException("Target '$fromName' is not an storage")

        val predicate: Predicate<ItemStack> = PeripheralPluginUtils.itemQueryToPredicate(itemQuery)
        val realLimit = min(itemStorageTransferLimit, limit.orElse(Int.MAX_VALUE))
        return storage.moveFrom(fromStorage, realLimit, takePredicate = predicate)
    }
}
