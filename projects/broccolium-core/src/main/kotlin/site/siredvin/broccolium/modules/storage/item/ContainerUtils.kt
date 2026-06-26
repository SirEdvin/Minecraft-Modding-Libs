package site.siredvin.broccolium.modules.storage.item

import net.minecraft.core.BlockPos
import net.minecraft.world.Container
import net.minecraft.world.Containers
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import java.util.function.Predicate
import kotlin.math.min

object ContainerUtils {
    fun extract(container: Container, slot: Int, limit: Int, previousStack: ItemStack, simulate: Boolean): ItemStack = extract(container, slot, limit, { previousStack.isEmpty || ItemStorageUtils.canStack(previousStack, it) }, simulate)

    fun extract(container: Container, slot: Int, limit: Int, predicate: Predicate<ItemStack>, simulate: Boolean): ItemStack {
        val existingStack = container.getItem(slot)
        if (existingStack.isEmpty || !predicate.test(existingStack)) {
            return ItemStack.EMPTY
        }
        if (simulate) {
            return existingStack.copyWithCount(min(existingStack.count, limit))
        }
        return container.removeItem(slot, limit)
    }

    fun takeItems(container: Container, limit: Int, startSlot: Int = 0, endSlot: Int = -1, predicate: Predicate<ItemStack>, simulate: Boolean): ItemStack {
        var slidingPredicate = predicate
        val realEndSlot = if (endSlot == -1) container.containerSize - 1 else endSlot
        var slidingLimit = limit
        var stack = ItemStack.EMPTY
        for (currentSlot in startSlot..realEndSlot) {
            if (limit <= 0) {
                return stack
            }
            val extractedStack = extract(container, currentSlot, slidingLimit, predicate, simulate)
            if (extractedStack.isEmpty) {
                continue
            }
            slidingLimit -= extractedStack.count
            if (stack.isEmpty) { // first time something successfully extracted
                stack = extractedStack
                slidingLimit = minOf(slidingLimit, stack.maxStackSize) - stack.count
                slidingPredicate = slidingPredicate.and {
                    ItemStorageUtils.canStack(stack, it)
                }
            } else {
                stack.grow(extractedStack.count)
            }
        }
        if (!simulate) {
            container.setChanged()
        }
        return stack
    }

    fun storeItem(container: Container, stack: ItemStack, startSlot: Int = 0, endSlot: Int = -1, simulate: Boolean): ItemStack {
        val maxStackSize = minOf(stack.maxStackSize, container.maxStackSize)
        val realEndSlot = if (endSlot == -1) container.containerSize - 1 else endSlot
        if (maxStackSize <= 0) {
            return stack
        }

        var slidingStack = stack
        for (currentSlot in startSlot..realEndSlot) {
            val slotStack = container.getItem(currentSlot)
            if (slotStack.isEmpty) {
                if (!container.canPlaceItem(currentSlot, slidingStack)) {
                    continue
                }
                if (slidingStack.count <= maxStackSize) {
                    if (!simulate) {
                        container.setItem(currentSlot, slidingStack)
                        container.setChanged()
                    }
                    return ItemStack.EMPTY
                } else {
                    if (!simulate) {
                        container.setItem(currentSlot, slidingStack.split(maxStackSize))
                    } else {
                        slidingStack.split(maxStackSize)
                    }
                }
            } else {
                val slotMaxStackSize = minOf(slotStack.maxStackSize, maxStackSize)
                if (slotStack.count >= slotMaxStackSize) {
                    continue
                }
                if (!ItemStorageUtils.canMerge(slotStack, slidingStack, slotMaxStackSize)) {
                    continue
                }

                slidingStack = if (!simulate) {
                    ItemStorageUtils.inplaceMerge(slotStack, slidingStack)
                } else {
                    ItemStorageUtils.inplaceMerge(slotStack.copy(), slidingStack)
                }

                if (slidingStack.isEmpty) {
                    if (!simulate) {
                        container.setChanged()
                    }
                    return ItemStack.EMPTY
                }
            }
        }
        if (!simulate) {
            container.setChanged()
        }
        return slidingStack
    }

    fun toInventoryOrToWorld(output: ItemStack, container: Container, startSlot: Int, outputPos: BlockPos, level: Level) {
        var rest = storeItem(container, output, startSlot, container.containerSize - 1, false)
        if (!rest.isEmpty && startSlot > 0) {
            rest = storeItem(container, output, 0, startSlot - 1, false)
        }
        if (!rest.isEmpty) {
            Containers.dropItemStack(level, outputPos.x.toDouble(), outputPos.y.toDouble(), outputPos.z.toDouble(), rest)
        }
    }
}
