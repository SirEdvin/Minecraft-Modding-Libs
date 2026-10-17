package site.siredvin.broccolium.modules.storage.item

import net.minecraft.world.Container
import net.minecraft.world.item.ItemStack
import site.siredvin.broccolium.modules.storage.base.api.SomethingOperator
import java.util.function.Predicate

/**
 * Wraps a container using its ordinary exact [Container.setItem] replacement semantics.
 * Custom containers used transactionally must obey [ReplaceableItemStorage], including the
 * absence of non-restorable side effects; use their native adapter instead when one is available.
 */
class ContainerWrapper(private val container: Container) : ReplaceableItemStorage {
    override fun take(limit: Int, startSlot: Int, endSlot: Int, predicate: Predicate<ItemStack>, simulate: Boolean): ItemStack = ContainerUtils.takeItems(container, limit, startSlot, endSlot, predicate, simulate)

    override fun get(slot: Int): ItemStack = container.getItem(slot)

    override fun replaceSlot(slot: Int, stack: ItemStack) {
        require(slot in 0 until size) { "Slot $slot is outside the container" }
        // Vanilla setters may clamp oversized stacks; refuse rather than silently lose items.
        require(stack.count in 0..minOf(maxStackSize, stack.maxStackSize)) { "Replacement exceeds the container's representable stack size" }
        container.setItem(slot, stack.copy())
    }

    override fun canPlace(slot: Int, item: ItemStack): Boolean = container.canPlaceItem(slot, item)
    override fun getLimit(slot: Int): Long = container.maxStackSize.toLong()

    override fun store(stack: ItemStack, startSlot: Int, endSlot: Int, simulate: Boolean): ItemStack = ContainerUtils.storeItem(container, stack, startSlot, endSlot, simulate)

    override fun setChanged() {
        container.setChanged()
    }

    override val size: Int
        get() = container.containerSize

    override val maxStackSize: Int
        get() = container.maxStackSize
    override val operator: SomethingOperator<ItemStack, Int>
        get() = ItemStorageUtils
}
