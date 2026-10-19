package site.siredvin.broccolium.modules.storage.item

import com.google.common.collect.MapMaker
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.transfer.access.ItemAccess
import net.neoforged.neoforge.transfer.item.ItemResource
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal
import net.neoforged.neoforge.transfer.transaction.TransactionContext
import site.siredvin.broccolium.modules.storage.base.api.SlottedAgnosticStorage
import java.lang.ref.WeakReference

/** Exact replacement is mandatory; additive store/take cannot implement rollback. */
internal class AgnosticItemAccess private constructor(
    private val storage: ReplaceableItemStorage,
    private val slot: Int,
) : SnapshotJournal<ItemStack>(),
    ItemAccess {
    companion object {
        private val wrappers: MutableMap<SlottedAgnosticStorage<ItemStack, Int>, MutableMap<Int, WeakReference<AgnosticItemAccess>>> =
            MapMaker().weakKeys().makeMap()

        fun of(origin: SlottedAgnosticStorage<ItemStack, Int>, slot: Int): ItemAccess {
            require(slot in 0 until origin.size) { "Invalid item slot" }
            if (origin is AgnosticItemHandlerWrapper) return ItemAccess.forHandlerIndexStrict(origin.handler, slot)
            val replaceable = origin as? ReplaceableItemStorage
                ?: throw UnsupportedOperationException("Transactional item capabilities require ReplaceableItemStorage or native NeoForge storage")
            val slots = wrappers.getOrPut(origin) { mutableMapOf() }
            return slots[slot]?.get() ?: AgnosticItemAccess(replaceable, slot).also { slots[slot] = WeakReference(it) }
        }
    }

    override fun getResource(): ItemResource = ItemResource.of(storage.get(slot))
    override fun getAmount(): Int = storage.get(slot).count

    override fun createSnapshot(): ItemStack = storage.get(slot).copy().also {
        if (it.count !in 0..minOf(storage.maxStackSize, it.maxStackSize)) {
            throw UnsupportedOperationException("ReplaceableItemStorage cannot restore an out-of-range stack")
        }
    }

    override fun revertToSnapshot(snapshot: ItemStack) {
        storage.replaceSlot(slot, snapshot)
    }

    override fun onRootCommit(originalState: ItemStack) {
        storage.setChanged()
    }

    override fun insert(resource: ItemResource, amount: Int, transaction: TransactionContext): Int {
        require(!resource.isEmpty && amount >= 0) { "Invalid item insertion" }
        if (amount == 0) return 0
        val current = storage.get(slot).copy()
        if (!current.isEmpty && !resource.matches(current)) return 0
        val offered = resource.toStack()
        if (!storage.canPlace(slot, offered)) return 0
        val capacity = minOf(storage.getLimit(slot).coerceIn(0, Int.MAX_VALUE.toLong()).toInt(), storage.maxStackSize, offered.maxStackSize)
        val inserted = minOf(amount, (capacity - current.count).coerceAtLeast(0))
        if (inserted == 0) return 0
        updateSnapshots(transaction)
        storage.replaceSlot(slot, resource.toStack(current.count + inserted))
        return inserted
    }

    override fun extract(resource: ItemResource, amount: Int, transaction: TransactionContext): Int {
        require(!resource.isEmpty && amount >= 0) { "Invalid item extraction" }
        val current = storage.get(slot).copy()
        if (amount == 0 || current.isEmpty || !resource.matches(current)) return 0
        val extracted = minOf(amount, current.count)
        updateSnapshots(transaction)
        storage.replaceSlot(slot, current.copyWithCount(current.count - extracted))
        return extracted
    }
}
