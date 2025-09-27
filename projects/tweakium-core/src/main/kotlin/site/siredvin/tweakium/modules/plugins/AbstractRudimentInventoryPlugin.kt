package site.siredvin.tweakium.modules.plugins

import dan200.computercraft.api.lua.IArguments
import dan200.computercraft.api.lua.LuaFunction
import net.minecraft.world.level.Level
import site.siredvin.broccolium.modules.storage.item.ItemStorageUtils
import site.siredvin.broccolium.modules.storage.item.api.SlottedAgnosticItemStorage
import site.siredvin.tweakium.modules.peripheral.api.IPeripheralPlugin
import site.siredvin.tweakium.modules.peripheral.representation.LuaRepresentation
import site.siredvin.tweakium.modules.peripheral.representation.RepresentationMode
import site.siredvin.tweakium.modules.peripheral.util.assertBetween

abstract class AbstractRudimentInventoryPlugin : IPeripheralPlugin {
    abstract val storage: SlottedAgnosticItemStorage
    abstract val level: Level

    override val additionalType: String
        get() = PeripheralPluginUtils.Type.INVENTORY_VIEW

    open fun sizeImpl(): Int = storage.size

    open fun listImpl(arguments: IArguments): Map<Int, Map<String, *>> {
        val detailed = arguments.optBoolean(0, false)
        val filterArg = arguments.get(1)
        val mode = if (detailed) RepresentationMode.DETAILED else RepresentationMode.BASE
        val filter = if (filterArg != null) {
            PeripheralPluginUtils.itemQueryToPredicate(filterArg)
        } else {
            ItemStorageUtils.ALWAYS
        }
        val result: MutableMap<Int, Map<String, *>> = hashMapOf()
        val size = storage.size
        for (i in 0 until size) {
            val stack = storage.getItem(i)
            if (!stack.isEmpty && filter.test(stack)) result[i + 1] = LuaRepresentation.forItemStack(stack, mode)
        }
        return result
    }

    open fun getItemDetailImpl(slot: Int): Map<String, *>? {
        val stack = storage.getItem(slot)
        return if (stack.isEmpty) null else LuaRepresentation.forItemStack(stack)
    }

    override fun collectConfiguration(data: MutableMap<String, Any>) {
        data["inventoryAPIVersion"] = listOf(1, 2)
    }

    open fun getItemLimitImpl(slot: Int): Long = storage.getItemLimit(slot)

    @LuaFunction(mainThread = true)
    fun size(): Int = sizeImpl()

    @LuaFunction(mainThread = true)
    fun list(arguments: IArguments): Map<Int, Map<String, *>> = listImpl(arguments)

    @LuaFunction(mainThread = true)
    fun getItemDetail(slot: Int): Map<String, *>? {
        assertBetween(slot, 1, storage.size, "slot")
        return getItemDetailImpl(slot - 1)
    }

    @LuaFunction(mainThread = true)
    fun getItemLimit(slot: Int): Long {
        assertBetween(slot, 1, storage.size, "slot")
        return getItemLimitImpl(slot - 1)
    }
}
