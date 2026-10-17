package site.siredvin.broccolium.modules.storage.item

import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.transfer.ResourceHandler
import net.neoforged.neoforge.transfer.item.ItemResource
import net.neoforged.neoforge.transfer.transaction.Transaction
import site.siredvin.broccolium.modules.storage.base.api.SlottedAgnosticStorage
import site.siredvin.broccolium.modules.storage.base.api.SomethingOperator
import java.util.function.Predicate

class AgnosticItemHandlerWrapper(internal val handler: ResourceHandler<ItemResource>) : SlottedAgnosticStorage<ItemStack, Int> {
    override fun take(limit: Int, startSlot: Int, endSlot: Int, predicate: Predicate<ItemStack>, simulate: Boolean): ItemStack {
        require(limit >= 0) { "Negative item limit" }
        if (limit == 0 || startSlot > endSlot) return ItemStack.EMPTY
        require(startSlot >= 0 && endSlot < size) { "Invalid item slot range" }
        Transaction.openRoot().use { transaction ->
            var selected: ItemResource? = null
            var extracted = 0
            for (slot in startSlot..endSlot) {
                val resource = handler.getResource(slot)
                if (resource.isEmpty || (selected != null && resource != selected)) continue
                val remaining = minOf(limit, resource.maxStackSize) - extracted
                if (remaining <= 0) break
                val preview = Transaction.open(transaction).use { preview -> handler.extract(slot, resource, remaining, preview) }
                if (preview == 0 || !predicate.test(resource.toStack(preview))) continue
                val amount = handler.extract(slot, resource, remaining, transaction)
                if (amount > 0) {
                    selected = resource
                    extracted += amount
                }
            }
            if (!simulate) transaction.commit()
            return selected?.toStack(extracted) ?: ItemStack.EMPTY
        }
    }

    override fun get(slot: Int): ItemStack = handler.getResource(slot).toStack(handler.getAmountAsInt(slot))
    override fun canPlace(slot: Int, item: ItemStack): Boolean = !item.isEmpty && handler.isValid(slot, ItemResource.of(item))
    override fun getLimit(slot: Int): Long = handler.getCapacityAsLong(slot, ItemResource.EMPTY)

    override fun store(stack: ItemStack, startSlot: Int, endSlot: Int, simulate: Boolean): ItemStack {
        if (stack.isEmpty || startSlot > endSlot) return stack.copy()
        require(startSlot >= 0 && endSlot < size) { "Invalid item slot range" }
        val resource = ItemResource.of(stack)
        Transaction.openRoot().use { transaction ->
            var inserted = 0
            for (slot in startSlot..endSlot) {
                inserted += handler.insert(slot, resource, stack.count - inserted, transaction)
                if (inserted == stack.count) break
            }
            if (!simulate) transaction.commit()
            return stack.copyWithCount(stack.count - inserted)
        }
    }

    override fun setChanged() {
    }

    override val size: Int
        get() = handler.size()
    override val maxStackSize: Int
        get() = (0 until size).minOfOrNull { handler.getCapacityAsInt(it, ItemResource.EMPTY) } ?: 0
    override val operator: SomethingOperator<ItemStack, Int>
        get() = ItemStorageUtils
}
