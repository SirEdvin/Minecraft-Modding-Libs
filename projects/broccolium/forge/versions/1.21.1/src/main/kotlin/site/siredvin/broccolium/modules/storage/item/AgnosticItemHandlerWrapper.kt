package site.siredvin.broccolium.modules.storage.item

import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.items.IItemHandler
import site.siredvin.broccolium.modules.storage.base.api.SlottedAgnosticStorage
import site.siredvin.broccolium.modules.storage.base.api.SomethingOperator
import java.util.function.Predicate
import kotlin.math.min

class AgnosticItemHandlerWrapper(private val handler: IItemHandler) : SlottedAgnosticStorage<ItemStack, Int> {
    override fun take(limit: Int, startSlot: Int, endSlot: Int, predicate: Predicate<ItemStack>, simulate: Boolean): ItemStack {
        var slidingLimit = limit
        var slidingItemStack = ItemStack.EMPTY
        for (currentSlot in startSlot..endSlot) {
            val tryExtractedStack = handler.extractItem(currentSlot, limit, true)
            if (tryExtractedStack.isEmpty) {
                continue
            }
            if (!predicate.test(tryExtractedStack)) {
                continue
            }
            val extractedStack = handler.extractItem(currentSlot, limit, simulate)
            if (slidingItemStack.isEmpty) {
                slidingItemStack = extractedStack
                // So, update actual limit to have a little more sense
                slidingLimit = minOf(slidingItemStack.maxStackSize, limit)
                slidingLimit -= extractedStack.count
            } else {
                if (ItemStorageUtils.canMerge(slidingItemStack, extractedStack)) {
                    val extractedCount = extractedStack.count
                    val remainExtracted = ItemStorageUtils.inplaceMerge(slidingItemStack, extractedStack)
                    if (!remainExtracted.isEmpty && !simulate) {
                        handler.insertItem(currentSlot, remainExtracted, false)
                    }
                    slidingLimit -= extractedCount - remainExtracted.count
                } else if (!simulate) {
                    handler.insertItem(currentSlot, extractedStack, false)
                }
            }
            if (slidingLimit <= 0) {
                break
            }
        }
        return slidingItemStack
    }

    override fun get(slot: Int): ItemStack = handler.getStackInSlot(slot)

    override fun canPlace(slot: Int, item: ItemStack): Boolean = true
    override fun getLimit(slot: Int): Long = handler.getSlotLimit(slot).toLong()

    override fun store(stack: ItemStack, startSlot: Int, endSlot: Int, simulate: Boolean): ItemStack {
        var slidingItemStack = stack
        for (currentSlot in startSlot..endSlot) {
            slidingItemStack = handler.insertItem(currentSlot, slidingItemStack, simulate)
            if (slidingItemStack.isEmpty) {
                break
            }
        }
        return slidingItemStack
    }

    override fun setChanged() {
    }

    override val size: Int
        get() = handler.slots

    override val maxStackSize: Int by lazy {
        var slidingLimit = Int.MAX_VALUE
        for (i in 0 until handler.slots) {
            slidingLimit = min(slidingLimit, handler.getSlotLimit(i))
        }
        return@lazy slidingLimit
    }
    override val operator: SomethingOperator<ItemStack, Int>
        get() = ItemStorageUtils
}
