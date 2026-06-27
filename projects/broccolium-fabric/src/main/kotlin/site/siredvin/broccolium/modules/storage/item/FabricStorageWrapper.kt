package site.siredvin.broccolium.modules.storage.item

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction
import net.minecraft.world.item.ItemStack
import site.siredvin.broccolium.BroccoliumCore
import site.siredvin.broccolium.modules.storage.FabricStorageUtils
import site.siredvin.broccolium.modules.storage.base.api.AgnosticSink
import site.siredvin.broccolium.modules.storage.base.api.AgnosticStorage
import site.siredvin.broccolium.modules.storage.base.api.SlottedAgnosticSink
import site.siredvin.broccolium.modules.storage.base.api.SomethingOperator
import java.util.function.Predicate
import net.fabricmc.fabric.api.transfer.v1.storage.Storage as FabricStorage

open class FabricStorageWrapper(internal val storage: FabricStorage<ItemVariant>) : AgnosticStorage<ItemStack, Int> {

    override val maxStackSize: Int by lazy {
        storage.nonEmptyViews().map { it.capacity }.min().toInt()
    }
    override val operator: SomethingOperator<ItemStack, Int>
        get() = ItemStorageUtils

    override fun moveTo(to: AgnosticSink<ItemStack, Int>, limit: Int, toSlot: Int, takePredicate: Predicate<ItemStack>): Int {
        if (to is FabricSlottedStorageWrapper) {
            return to.moveFrom(this, limit, toSlot, -1, takePredicate)
        }
        if (to is FabricStorageWrapper) {
            if (toSlot > -1) {
                BroccoliumCore.LOGGER.warn("To storage doesn't support slotting, so slot is just ignored")
            }
            return to.moveFrom(this, limit, -1, takePredicate)
        }
        if (toSlot < -1 && to !is SlottedAgnosticSink<ItemStack, Int>) {
            BroccoliumCore.LOGGER.warn("To storage doesn't support slotting, so slot is just ignored")
            return FabricStorageUtils.moveToTargetable(this.storage, to, limit, -1, takePredicate)
        }
        return FabricStorageUtils.moveToTargetable(this.storage, to, limit, toSlot, takePredicate)
    }

    override fun moveFrom(
        from: AgnosticStorage<ItemStack, Int>,
        limit: Int,
        fromSlot: Int,
        takePredicate: Predicate<ItemStack>,
    ): Int {
        if (from is FabricSlottedStorageWrapper) {
            if (fromSlot > 0) {
                val slotStorage = from.getSingleSlot(fromSlot)
                return StorageUtil.move(
                    slotStorage,
                    storage,
                    FabricStorageUtils.wrapItem(takePredicate),
                    limit.toLong(),
                    null,
                ).toInt()
            }
            return StorageUtil.move(from.storage, storage, FabricStorageUtils.wrapItem(takePredicate), limit.toLong(), null).toInt()
            // TODO: catch this case with testing!
        }
        if (from is FabricStorageWrapper) {
            if (fromSlot > -1) {
                BroccoliumCore.LOGGER.warn("From storage doesn't support slotting, so slot is just ignored")
            }
            return StorageUtil.move(from.storage, storage, FabricStorageUtils.wrapItem(takePredicate), limit.toLong(), null).toInt()
        }
        return FabricStorageUtils.moveFromTargetable(from, storage, limit, fromSlot, takePredicate)
    }

    override val movableType: String
        get() = FabricStorageUtils.MOVABLE_TYPE

    override fun getContent(): Iterator<ItemStack> = SlidingIterator(storage.iterator())

    override fun take(predicate: Predicate<ItemStack>, limit: Int, simulate: Boolean): ItemStack {
        Transaction.openOuter().use {
            val extractionTarget = StorageUtil.findExtractableContent(storage, FabricStorageUtils.wrapItem(predicate), it)
                ?: return ItemStack.EMPTY
            if (extractionTarget.amount == 0L) {
                return ItemStack.EMPTY
            }
            val amount = storage.extract(extractionTarget.resource, limit.coerceAtMost(extractionTarget.resource.toStack().maxStackSize).toLong(), it)
            if (amount < 1) {
                it.abort()
                return ItemStack.EMPTY
            }
            if (!simulate) {
                it.commit()
            } else {
                it.abort()
            }
            return extractionTarget.resource.toStack(amount.toInt())
        }
    }

    override fun store(stack: ItemStack, simulate: Boolean): ItemStack {
        Transaction.openOuter().use {
            val amount = storage.insert(ItemVariant.of(stack), stack.count.toLong(), it).toInt()
            stack.shrink(amount)
            if (!simulate) {
                it.commit()
            } else {
                it.abort()
            }
            if (stack.isEmpty) {
                return ItemStack.EMPTY
            }
            return stack
        }
    }

    override fun setChanged() {
    }
}
