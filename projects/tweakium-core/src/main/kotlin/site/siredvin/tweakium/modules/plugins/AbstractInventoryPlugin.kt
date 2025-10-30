package site.siredvin.tweakium.modules.plugins

import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.api.peripheral.IComputerAccess
import dan200.computercraft.api.peripheral.IPeripheral
import site.siredvin.broccolium.modules.storage.item.AgnosticItemStorageLookup
import site.siredvin.broccolium.modules.storage.item.ItemStorageUtils
import site.siredvin.broccolium.modules.storage.item.api.SlottedAgnosticItemSink
import site.siredvin.broccolium.modules.storage.item.api.SlottedAgnosticItemStorage
import site.siredvin.tweakium.modules.peripheral.util.assertBetween
import java.util.*

abstract class AbstractInventoryPlugin : AbstractRudimentInventoryPlugin() {

    override val additionalType: String
        get() = PeripheralPluginUtils.Type.INVENTORY

    override val additionalTypes: List<String>
        get() = listOf(PeripheralPluginUtils.Type.INVENTORY, PeripheralPluginUtils.Type.INVENTORY_EXTENDED)

    @LuaFunction(mainThread = true)
    @Throws(LuaException::class)
    fun pushItems(computer: IComputerAccess, toName: String, fromSlot: Any, limit: Optional<Int>, toSlot: Optional<Int>): Int {
        // Find location to transfer to
        val location: IPeripheral = computer.getAvailablePeripheral(toName)
            ?: throw LuaException("Target '$toName' does not exist")

        val toStorage = AgnosticItemStorageLookup.extractItemSinkFromUnknown(level, location.target)
            ?: throw LuaException("Target '$toName' is not an inventory")

        // Validate slots

        // Validate slots
        val actualLimit: Int = limit.orElse(Int.MAX_VALUE)
        if (actualLimit <= 0) {
            return 0
        }
        if (toSlot.isPresent) {
            if (toStorage !is SlottedAgnosticItemSink) {
                throw LuaException("Target '$toName' is not slotted storage, so you can't provide slot")
            }
            assertBetween(toSlot.get(), 1, toStorage.size, "toSlot")
        }
        if (fromSlot is Number) {
            assertBetween(fromSlot.toInt(), 1, storage.size, "fromSlot")
            return storage.moveTo(toStorage, actualLimit, fromSlot.toInt() - 1, toSlot.orElse(0) - 1, ItemStorageUtils.ALWAYS)
        }
        return storage.moveTo(toStorage, actualLimit, toSlot.orElse(0) - 1, PeripheralPluginUtils.itemQueryToPredicate(fromSlot))
    }

    @LuaFunction(mainThread = true)
    @Throws(LuaException::class)
    fun pullItems(computer: IComputerAccess, fromName: String, fromSlot: Any, limit: Optional<Int>, toSlot: Optional<Int>): Int {
        // Find location to transfer to
        val location =
            computer.getAvailablePeripheral(fromName) ?: throw LuaException("Source '$fromName' does not exist")
        val fromStorage = AgnosticItemStorageLookup.extractStorageFromUnknown(level, location.target)
            ?: throw LuaException("Source '$fromName' is not an inventory")

        // Validate slots
        val actualLimit = limit.orElse(Int.MAX_VALUE)
        if (actualLimit <= 0) {
            return 0
        }
        if (fromSlot is Number) {
            if (fromStorage !is SlottedAgnosticItemStorage) {
                throw LuaException("Source '$fromName' is not slotted storage")
            }
            assertBetween(fromSlot.toInt(), 1, fromStorage.size, "fromSlot")
            return storage.moveFrom(fromStorage, actualLimit, toSlot.orElse(0) - 1, fromSlot.toInt() - 1, ItemStorageUtils.ALWAYS)
        }
        if (toSlot.isPresent) {
            assertBetween(toSlot.get(), 1, storage.size, "toSlot")
        }
        return storage.moveFrom(fromStorage, actualLimit, takePredicate = PeripheralPluginUtils.itemQueryToPredicate(fromSlot))
    }
}
