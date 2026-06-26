package site.siredvin.broccolium.modules.storage.item

import net.minecraft.world.Container
import net.minecraft.world.item.ItemStack
import site.siredvin.broccolium.modules.storage.base.api.SlottedAgnosticStorage
import site.siredvin.broccolium.modules.storage.base.api.SomethingOperator
import java.util.function.Predicate

class ContainerWrapper(private val container: Container) : SlottedAgnosticStorage<ItemStack, Int> {
    override fun take(limit: Int, startSlot: Int, endSlot: Int, predicate: Predicate<ItemStack>, simulate: Boolean): ItemStack = ContainerUtils.takeItems(container, limit, startSlot, endSlot, predicate, simulate)

    override fun get(slot: Int): ItemStack = container.getItem(slot)

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
