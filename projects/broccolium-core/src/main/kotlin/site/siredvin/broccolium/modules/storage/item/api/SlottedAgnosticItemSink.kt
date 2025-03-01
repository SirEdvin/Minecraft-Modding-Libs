package site.siredvin.broccolium.modules.storage.item.api

import net.minecraft.world.item.ItemStack
import site.siredvin.broccolium.BroccoliumCore
import site.siredvin.broccolium.modules.storage.item.ItemStorageUtils
import java.util.function.Predicate

interface SlottedAgnosticItemSink : AgnosticItemSink {
    val size: Int

    fun storeItem(stack: ItemStack, startSlot: Int, endSlot: Int): ItemStack

    fun storeItem(stack: ItemStack, startSlot: Int): ItemStack = storeItem(stack, startSlot, size - 1)
    override fun storeItem(stack: ItemStack): ItemStack = storeItem(stack, 0, size - 1)

    fun moveFrom(from: AgnosticItemStorage, limit: Int, toSlot: Int = -1, fromSlot: Int = -1, takePredicate: Predicate<ItemStack>): Int {
        if (movableType != null) {
            throw IllegalStateException("With movable type you should redefine this function")
        }
        if (from.movableType == null) {
            return ItemStorageUtils.naiveMove(
                from,
                this,
                limit,
                fromSlot,
                toSlot,
                takePredicate,
            )
        }
        if (fromSlot < 0) {
            return from.moveTo(this, limit, toSlot, takePredicate)
        }
        if (from !is SlottedAgnosticItemStorage) {
            BroccoliumCore.LOGGER.warn("From storage doesn't support slotting, so we just ignore slot here")
            return from.moveTo(this, limit, toSlot, takePredicate)
        }
        return from.moveTo(this, limit, fromSlot, toSlot, takePredicate)
    }

    override fun moveFrom(
        from: AgnosticItemStorage,
        limit: Int,
        fromSlot: Int,
        takePredicate: Predicate<ItemStack>,
    ): Int = moveFrom(from, limit, -1, fromSlot, takePredicate)
}
