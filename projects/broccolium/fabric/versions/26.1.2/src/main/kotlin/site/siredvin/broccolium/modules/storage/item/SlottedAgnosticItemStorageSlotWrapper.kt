package site.siredvin.broccolium.modules.storage.item

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import net.minecraft.world.item.ItemStack

class SlottedAgnosticItemStorageSlotWrapper(private val storage: SlottedAgnosticItemStorageWrapper, private val slot: Int) : SingleStackStorage() {
    override fun getStack(): ItemStack = storage.storage.get(slot).copy()

    override fun setStack(stack: ItemStack) {
        storage.replaceableStorage.replaceSlot(slot, stack)
    }

    override fun createSnapshot(): ItemStack = getStack().also {
        if (it.count !in 0..minOf(storage.storage.maxStackSize, it.maxStackSize)) {
            throw UnsupportedOperationException("ReplaceableItemStorage cannot restore an out-of-range stack")
        }
    }

    override fun canInsert(itemVariant: ItemVariant): Boolean = storage.storage.canPlace(slot, itemVariant.toStack())

    public override fun getCapacity(variant: ItemVariant): Int = minOf(
        storage.storage.getLimit(slot).coerceIn(0, Int.MAX_VALUE.toLong()).toInt(),
        storage.storage.maxStackSize,
        super.getCapacity(variant),
    )

    override fun updateSnapshots(transaction: TransactionContext) {
        super.updateSnapshots(transaction)
        storage.markDirtyParticipant.updateSnapshots(transaction)
    }
}
