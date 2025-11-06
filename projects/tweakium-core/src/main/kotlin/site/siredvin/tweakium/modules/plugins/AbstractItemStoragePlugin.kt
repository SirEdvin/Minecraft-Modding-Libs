package site.siredvin.tweakium.modules.plugins

import dan200.computercraft.api.lua.IArguments
import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.api.peripheral.IComputerAccess
import dan200.computercraft.api.peripheral.IPeripheral
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import site.siredvin.broccolium.modules.storage.item.AgnosticItemSinkLookup
import site.siredvin.broccolium.modules.storage.item.AgnosticItemStorageLookup
import site.siredvin.broccolium.modules.storage.item.ItemStorageUtils
import site.siredvin.broccolium.modules.storage.item.api.AgnosticItemStorage
import site.siredvin.tweakium.modules.peripheral.api.IPeripheralPlugin
import site.siredvin.tweakium.modules.peripheral.api.ISidedPeripheral
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

    open fun itemsImpl(mode: RepresentationMode = RepresentationMode.DETAILED, filter: Any?): List<MutableMap<String, *>> {
        val result: MutableList<MutableMap<String, *>> = mutableListOf()
        val predicate = if (filter != null) {
            PeripheralPluginUtils.itemQueryToPredicate(filter)
        } else {
            ItemStorageUtils.ALWAYS
        }
        storage.getItems().forEach {
            if (!it.isEmpty && predicate.test(it)) {
                result.add(LuaRepresentation.forItemStack(it, mode))
            }
        }
        return result
    }

    override fun collectConfiguration(data: MutableMap<String, Any>) {
        data["itemStorageTransferLimit"] = itemStorageTransferLimit
        data["itemStorageAPI"] = listOf(1, 1)
    }

    @LuaFunction(mainThread = true)
    fun items(arguments: IArguments): List<Map<String, *>> {
        val isDetailed = arguments.optBoolean(0, true)
        return itemsImpl(mode = if (isDetailed) RepresentationMode.DETAILED else RepresentationMode.BASE, arguments.get(1))
    }

    @LuaFunction(mainThread = true)
    fun pushItem(computer: IComputerAccess, toName: String, itemQuery: Any?, limit: Optional<Int>): Int {
        val location: IPeripheral = computer.getAvailablePeripheral(toName)
            ?: throw LuaException("Target '$toName' does not exist")

        val direction = if (location is ISidedPeripheral) location.side else null

        val toStorage = AgnosticItemSinkLookup.extractFromUnknown(level, location.target, direction)
            ?: throw LuaException("Target '$toName' is not an targetable storage")

        val predicate: Predicate<ItemStack> = PeripheralPluginUtils.itemQueryToPredicate(itemQuery)
        val realLimit = min(itemStorageTransferLimit, limit.orElse(Int.MAX_VALUE))
        return storage.moveTo(toStorage, realLimit, takePredicate = predicate)
    }

    @LuaFunction(mainThread = true)
    fun pullItem(computer: IComputerAccess, fromName: String, itemQuery: Any?, limit: Optional<Int>): Int {
        val location: IPeripheral = computer.getAvailablePeripheral(fromName)
            ?: throw LuaException("Target '$fromName' does not exist")

        val direction = if (location is ISidedPeripheral) location.side else null

        val fromStorage = AgnosticItemStorageLookup.extractFromUnknown(level, location.target, direction)
            ?: throw LuaException("Target '$fromName' is not an storage")

        val predicate: Predicate<ItemStack> = PeripheralPluginUtils.itemQueryToPredicate(itemQuery)
        val realLimit = min(itemStorageTransferLimit, limit.orElse(Int.MAX_VALUE))
        return storage.moveFrom(fromStorage, realLimit, takePredicate = predicate)
    }
}
