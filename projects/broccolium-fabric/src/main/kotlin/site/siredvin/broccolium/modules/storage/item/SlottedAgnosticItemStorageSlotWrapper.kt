package site.siredvin.broccolium.modules.storage.item

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import net.fabricmc.fabric.impl.transfer.item.ItemVariantImpl
import net.minecraft.world.item.ItemStack
import kotlin.math.min

class SlottedAgnosticItemStorageSlotWrapper(private val storage: SlottedAgnosticItemStorageWrapper, private val slot: Int) : SingleStackStorage() {
    private var lastReleasedSnapshot: ItemStack? = null

    override fun getStack(): ItemStack = storage.storage.get(slot)

    override fun setStack(stack: ItemStack) {
        storage.storage.store(stack, slot, slot, false)
    }

    override fun insert(insertedVariant: ItemVariant, maxAmount: Long, transaction: TransactionContext?): Long {
        if (!canInsert(slot, (insertedVariant as ItemVariantImpl).getCachedStack())) {
            return 0
        }

        val ret = super.insert(insertedVariant, maxAmount, transaction)
        return ret
    }

    private fun canInsert(slot: Int, stack: ItemStack): Boolean = storage.storage.canPlace(slot, stack)

    override fun extract(variant: ItemVariant?, maxAmount: Long, transaction: TransactionContext?): Long {
        val ret = super.extract(variant, maxAmount, transaction)
        return ret
    }

    public override fun getCapacity(variant: ItemVariant): Int = min(storage.storage.maxStackSize, variant.item.defaultMaxStackSize)

    // We override updateSnapshots to also schedule a markDirty call for the backing inventory.
    override fun updateSnapshots(transaction: TransactionContext?) {
        storage.markDirtyParticipant.updateSnapshots(transaction)
        super.updateSnapshots(transaction)
    }

    override fun releaseSnapshot(snapshot: ItemStack?) {
        lastReleasedSnapshot = snapshot
    }

    override fun onFinalCommit() {
        // Try to apply the change to the original stack
        val original = lastReleasedSnapshot
        val currentStack = stack

        if (!original!!.isEmpty && ItemStorageUtils.canStack(original, currentStack)) {
            // Neither is empty and item components match: just update the amount and reuse the original stack.
            original.count = currentStack.count
            stack = original
        } else {
            // Otherwise assume everything was taken from original so empty it.
            original.count = 0
        }
    }
}
