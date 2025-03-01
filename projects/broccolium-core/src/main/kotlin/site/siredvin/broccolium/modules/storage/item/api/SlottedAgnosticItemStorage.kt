package site.siredvin.broccolium.modules.storage.item.api

import net.minecraft.world.item.ItemStack
import site.siredvin.broccolium.BroccoliumCore
import site.siredvin.broccolium.modules.storage.item.ItemStorageUtils
import site.siredvin.broccolium.modules.storage.item.SlottedStorageIterator
import java.util.function.Predicate

interface SlottedAgnosticItemStorage :
    AgnosticItemStorage,
    SlottedAgnosticItemSink,
    AccessibleAgnosticItemStorage {
    fun takeItems(limit: Int, startSlot: Int, endSlot: Int, predicate: Predicate<ItemStack>): ItemStack

    fun canPlaceItem(slot: Int, item: ItemStack): Boolean

    override fun getItems(): Iterator<ItemStack> = SlottedStorageIterator(this)

    override fun takeItems(predicate: Predicate<ItemStack>, limit: Int): ItemStack = takeItems(limit, 0, size - 1, predicate)

    fun moveTo(to: AgnosticItemSink, limit: Int, fromSlot: Int = -1, toSlot: Int = -1, takePredicate: Predicate<ItemStack>): Int {
        if (movableType != null) {
            throw IllegalStateException("With movable type you should redefine this function")
        }
        if (to.movableType == null) {
            return ItemStorageUtils.naiveMove(this, to, limit, fromSlot, toSlot, takePredicate)
        }
        if (toSlot < 0) {
            return to.moveFrom(this, limit, fromSlot, takePredicate)
        }
        if (to !is SlottedAgnosticItemSink) {
            BroccoliumCore.LOGGER.warn("To storage doesn't support slotting, so we just ignore slot here")
            return to.moveFrom(this, limit, fromSlot, takePredicate)
        }
        return to.moveFrom(this, limit, toSlot, fromSlot, takePredicate)
    }

    override fun moveTo(to: AgnosticItemSink, limit: Int, toSlot: Int, takePredicate: Predicate<ItemStack>): Int = moveTo(to, limit, -1, toSlot, takePredicate)
}
