package site.siredvin.broccolium.modules.storage.item.api

import net.minecraft.world.item.ItemStack
import site.siredvin.broccolium.BroccoliumCore
import site.siredvin.broccolium.modules.storage.item.ItemStorageUtils
import java.util.function.Predicate

interface AgnosticItemStorage : AgnosticItemSink {
    fun getItems(): Iterator<ItemStack>
    fun takeItems(predicate: Predicate<ItemStack>, limit: Int): ItemStack

    fun moveTo(to: AgnosticItemSink, limit: Int, toSlot: Int = -1, takePredicate: Predicate<ItemStack>): Int {
        if (movableType != null) {
            throw IllegalStateException("With movable type you should redefine this function")
        }
        if (to.movableType == null) {
            return ItemStorageUtils.naiveMove(this, to, limit, -1, toSlot, takePredicate)
        }
        if (toSlot < 0) {
            return to.moveFrom(this, limit, -1, takePredicate)
        }
        if (to !is SlottedAgnosticItemSink) {
            BroccoliumCore.LOGGER.warn("To storage doesn't support slotting, so we just ignore slot here")
            return to.moveFrom(this, limit, -1, takePredicate)
        }
        return to.moveFrom(this, limit, toSlot, -1, takePredicate)
    }
}
