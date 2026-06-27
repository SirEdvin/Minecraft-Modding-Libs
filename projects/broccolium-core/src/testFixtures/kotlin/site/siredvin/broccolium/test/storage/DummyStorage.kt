package site.siredvin.broccolium.test.storage

import net.minecraft.world.item.ItemStack
import site.siredvin.broccolium.modules.storage.base.api.AccessibleAgnosticStorage
import site.siredvin.broccolium.modules.storage.base.api.SomethingOperator
import site.siredvin.broccolium.modules.storage.item.ItemStorageUtils
import java.util.function.Predicate

open class DummyStorage(private val maxSlots: Int, initialItems: List<ItemStack>) : AccessibleAgnosticStorage<ItemStack, Int> {

    val items: MutableList<ItemStack> = mutableListOf()

    override val maxStackSize: Int
        get() = 64
    override val operator: SomethingOperator<ItemStack, Int>
        get() = ItemStorageUtils

    init {
        if (initialItems.size > maxSlots) {
            throw IllegalArgumentException("Max slots is too low for you?")
        }
        initialItems.forEach {
            items.add(it)
        }
        clean()
    }

    override fun get(slot: Int): ItemStack = items[slot]

    override fun getContent(): Iterator<ItemStack> = items.iterator()

    fun clean() {
        items.removeIf { it.isEmpty }
    }

    override fun take(predicate: Predicate<ItemStack>, limit: Int, simulate: Boolean): ItemStack {
        var slidingStack = ItemStack.EMPTY
        var slidingLimit = limit
        val toRemove = mutableListOf<Int>()
        items.forEachIndexed { index, stack ->
            if (slidingLimit > 0) {
                if (!stack.isEmpty && predicate.test(stack)) {
                    if (slidingStack.isEmpty) {
                        val extractedStack = if (simulate) stack.copy() else stack
                        if (extractedStack.count > limit) {
                            slidingStack = extractedStack.split(limit)
                        } else {
                            slidingStack = extractedStack
                            toRemove.add(index)
                        }
                        slidingLimit = minOf(limit, stack.maxStackSize) - slidingStack.count
                    } else if (ItemStorageUtils.canMerge(slidingStack, stack)) {
                        val originalCount = stack.count
                        val remainder = ItemStorageUtils.inplaceMerge(slidingStack, if (simulate) stack.copy() else stack)
                        slidingLimit -= originalCount - remainder.count
                        if (remainder.isEmpty) {
                            toRemove.add(index)
                        }
                    }
                }
            }
        }
        if (!simulate) {
            toRemove.asReversed().forEach {
                items.removeAt(it)
            }
            clean()
        }
        return slidingStack
    }

    override fun store(stack: ItemStack, simulate: Boolean): ItemStack {
        items.forEach {
            if (ItemStorageUtils.canMerge(it, stack)) {
                ItemStorageUtils.inplaceMerge(if (simulate) it.copy() else it, stack)
            }
        }
        if (stack.isEmpty) {
            return ItemStack.EMPTY
        }
        if (items.size < maxSlots) {
            if (!simulate) {
                items.add(stack)
            }
            return ItemStack.EMPTY
        }
        return stack
    }

    override fun setChanged() {
    }
}
