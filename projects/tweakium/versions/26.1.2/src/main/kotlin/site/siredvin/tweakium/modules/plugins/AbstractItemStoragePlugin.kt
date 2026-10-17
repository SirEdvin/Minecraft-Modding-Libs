package site.siredvin.tweakium.modules.plugins

import dan200.computercraft.api.lua.IArguments
import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.api.peripheral.IComputerAccess
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import site.siredvin.broccolium.modules.storage.base.api.AgnosticStorage
import site.siredvin.broccolium.modules.storage.item.AgnosticItemSinkLookup
import site.siredvin.broccolium.modules.storage.item.AgnosticItemStorageLookup
import site.siredvin.broccolium.modules.storage.item.ItemStorageUtils
import site.siredvin.tweakium.modules.peripheral.api.IPeripheralPlugin
import site.siredvin.tweakium.modules.peripheral.api.ISidedPeripheral
import site.siredvin.tweakium.modules.peripheral.representation.LuaRepresentation
import site.siredvin.tweakium.modules.peripheral.representation.RepresentationMode
import java.util.Optional

abstract class AbstractItemStoragePlugin : IPeripheralPlugin {
    abstract val storage: AgnosticStorage<ItemStack, Int>
    abstract val level: Level
    abstract val itemStorageTransferLimit: Int
    override val additionalType: String
        get() = PeripheralPluginUtils.Type.ITEM_STORAGE

    open fun itemsImpl(mode: RepresentationMode = RepresentationMode.DETAILED, filter: Any?): List<MutableMap<String, *>> {
        val predicate = if (filter != null) PeripheralPluginUtils.itemQueryToPredicate(filter) else ItemStorageUtils.ALWAYS
        return storage.getContent().asSequence().filter { !it.isEmpty && predicate.test(it) }.map { LuaRepresentation.forItemStack(it, mode) }.toList()
    }

    override fun collectConfiguration(data: MutableMap<String, Any>) {
        data["itemStorageTransferLimit"] = itemStorageTransferLimit
        data["itemStorageAPI"] = listOf(1, 1)
    }

    @LuaFunction(mainThread = true)
    fun items(arguments: IArguments): List<Map<String, *>> = itemsImpl(if (arguments.optBoolean(0, true)) RepresentationMode.DETAILED else RepresentationMode.BASE, arguments.get(1))

    @LuaFunction(mainThread = true, value = ["pushItem"])
    fun pushItemLua(computer: IComputerAccess, arguments: IArguments): Int = pushItem(computer, arguments.getString(0), arguments.get(1), arguments.optionalTransferInt(2))

    @LuaFunction(mainThread = true, value = ["pullItem"])
    fun pullItemLua(computer: IComputerAccess, arguments: IArguments): Int = pullItem(computer, arguments.getString(0), arguments.get(1), arguments.optionalTransferInt(2))

    fun pushItem(computer: IComputerAccess, toName: String, itemQuery: Any?, limit: Optional<Int>): Int {
        val location = computer.getAvailablePeripheral(toName) ?: throw LuaException("Target '$toName' does not exist")
        val direction = if (location is ISidedPeripheral) location.side else null
        val toStorage = AgnosticItemSinkLookup.extractFromUnknown(level, location.target, direction) ?: throw LuaException("Target '$toName' is not an targetable storage")
        val predicate = PeripheralPluginUtils.itemQueryToPredicate(itemQuery)
        return storage.moveTo(toStorage, minOf(itemStorageTransferLimit, limit.orElse(Int.MAX_VALUE)), takePredicate = predicate)
    }

    fun pullItem(computer: IComputerAccess, fromName: String, itemQuery: Any?, limit: Optional<Int>): Int {
        val location = computer.getAvailablePeripheral(fromName) ?: throw LuaException("Target '$fromName' does not exist")
        val direction = if (location is ISidedPeripheral) location.side else null
        val fromStorage = AgnosticItemStorageLookup.extractFromUnknown(level, location.target, direction) ?: throw LuaException("Target '$fromName' is not an storage")
        val predicate = PeripheralPluginUtils.itemQueryToPredicate(itemQuery)
        return storage.moveFrom(fromStorage, minOf(itemStorageTransferLimit, limit.orElse(Int.MAX_VALUE)), takePredicate = predicate)
    }
}
