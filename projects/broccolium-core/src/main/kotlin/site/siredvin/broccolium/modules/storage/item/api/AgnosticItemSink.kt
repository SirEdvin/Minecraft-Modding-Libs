package site.siredvin.broccolium.modules.storage.item.api

import net.minecraft.world.item.ItemStack
import site.siredvin.broccolium.BroccoliumCore
import site.siredvin.broccolium.modules.storage.item.ItemStorageUtils
import java.util.function.Predicate

interface AgnosticItemSink {
    /**
     * Minimal storage abstraction, that can be used to store item in
     * Mostly used for item_storage and inventory plugins
     */
    fun moveFrom(from: AgnosticItemStorage, limit: Int, fromSlot: Int = -1, takePredicate: Predicate<ItemStack>): Int {
        if (movableType != null) {
            throw IllegalStateException("With movable type you should redefine this function")
        }
        if (from.movableType == null) {
            return ItemStorageUtils.naiveMove(from, this, limit, fromSlot, -1, takePredicate)
        }
        if (fromSlot < 0) {
            return from.moveTo(this, limit, -1, takePredicate)
        }
        if (from !is SlottedAgnosticItemStorage) {
            BroccoliumCore.LOGGER.warn("From storage doesn't support slotting, so we just ignore slot here")
            return from.moveTo(this, limit, -1, takePredicate)
        }
        return from.moveTo(this, limit, fromSlot, -1, takePredicate)
    }
    fun storeItem(stack: ItemStack): ItemStack
    fun setChanged()

    val maxStackSize: Int
    val movableType: String?
        get() = null
}
