package site.siredvin.broccolium.modules.storage.item

import net.minecraft.core.BlockPos
import net.minecraft.world.Containers
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import site.siredvin.broccolium.BroccoliumCore
import site.siredvin.broccolium.modules.storage.item.api.AgnosticItemSink
import site.siredvin.broccolium.modules.storage.item.api.AgnosticItemStorage
import site.siredvin.broccolium.modules.storage.item.api.SlottedAgnosticItemStorage
import java.util.function.Predicate

@Suppress("MemberVisibilityCanBePrivate")
object ItemStorageUtils {

    val ALWAYS: Predicate<ItemStack> = Predicate { true }

    fun naiveMove(from: AgnosticItemStorage, to: AgnosticItemSink, limit: Int, fromSlot: Int = -1, toSlot: Int = -1, takePredicate: Predicate<ItemStack>): Int {
        var slidingLimit = limit
        var slidingCount = 0
        while (slidingLimit > 0) {
            // Get stack to move
            val stack = if (fromSlot < 0) {
                from.takeItems(takePredicate, limit)
            } else {
                if (from !is SlottedAgnosticItemStorage) {
                    BroccoliumCore.LOGGER.warn("From storage doesn't support slotting, so we just ignoring it")
                    from.takeItems(takePredicate, limit)
                } else {
                    from.takeItems(limit, fromSlot, fromSlot, takePredicate)
                }
            }
            if (stack.isEmpty) {
                return slidingCount
            }

            val stackCount = stack.count

            // Move item to
            val remainder = if (toSlot < 0 || to !is SlottedAgnosticItemStorage) {
                to.storeItem(stack)
            } else {
                to.storeItem(stack, toSlot, toSlot)
            }

            // Calculate items moved
            val movedCount = stackCount - remainder.count
            if (!remainder.isEmpty) {
                // Put reminder back
                if (fromSlot < 0 || from !is SlottedAgnosticItemStorage) {
                    from.storeItem(remainder)
                } else {
                    from.storeItem(remainder, fromSlot, fromSlot)
                }
            }
            // Break cycle if nothing can be stored in target
            if (movedCount == 0) {
                return slidingCount
            }
            slidingLimit -= movedCount
            slidingCount += movedCount
        }
        return slidingCount
    }

    fun canStack(first: ItemStack, second: ItemStack): Boolean {
        if (!ItemStack.isSameItem(first, second)) {
            return false
        }
        return first.damageValue == second.damageValue && ItemStack.isSameItemSameTags(first, second)
    }

    fun canMerge(first: ItemStack, second: ItemStack, stackLimit: Int = -1): Boolean {
        if (!canStack(first, second)) {
            return false
        }
        val realStackLimit = if (stackLimit == -1) first.maxStackSize else minOf(stackLimit, first.maxStackSize)
        return first.count < realStackLimit
    }

    /**
     * Merge second item stack into first one and returns remains
     */
    fun inplaceMerge(first: ItemStack, second: ItemStack, mergeLimit: Int = Int.MAX_VALUE): ItemStack {
        if (!canMerge(first, second, mergeLimit)) {
            return second
        }
        val mergeSize = minOf(second.count, first.maxStackSize - first.count, mergeLimit)
        first.grow(mergeSize)
        second.shrink(mergeSize)
        if (second.isEmpty) {
            return ItemStack.EMPTY
        }
        return second
    }

    fun toInventoryOrToWorld(output: ItemStack, inventory: AgnosticItemSink, outputPos: BlockPos, level: Level) {
        val rest = inventory.storeItem(output)
        if (!rest.isEmpty) {
            Containers.dropItemStack(level, outputPos.x.toDouble(), outputPos.y.toDouble(), outputPos.z.toDouble(), rest)
        }
    }

    fun toInventoryOrToWorld(output: ItemStack, inventory: SlottedAgnosticItemStorage, startSlot: Int, outputPos: BlockPos, level: Level) {
        var rest = inventory.storeItem(output, startSlot)
        if (!rest.isEmpty && startSlot > 0) {
            rest = inventory.storeItem(rest, 0, startSlot - 1)
        }
        if (!rest.isEmpty) {
            Containers.dropItemStack(level, outputPos.x.toDouble(), outputPos.y.toDouble(), outputPos.z.toDouble(), rest)
        }
    }
}
