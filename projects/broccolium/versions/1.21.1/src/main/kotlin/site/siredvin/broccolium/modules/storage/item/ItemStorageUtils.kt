package site.siredvin.broccolium.modules.storage.item

import net.minecraft.core.BlockPos
import net.minecraft.world.Containers
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import site.siredvin.broccolium.modules.storage.base.api.AgnosticSink
import site.siredvin.broccolium.modules.storage.base.api.SlottedAgnosticStorage
import site.siredvin.broccolium.modules.storage.base.api.SomethingOperator
import java.util.function.Predicate

@Suppress("MemberVisibilityCanBePrivate")
object ItemStorageUtils : SomethingOperator<ItemStack, Int> {

    val ALWAYS: Predicate<ItemStack> = Predicate { true }
    override fun isEmpty(something: ItemStack): Boolean = something.isEmpty

    override fun getSize(something: ItemStack): Int = something.count

    override fun canStack(first: ItemStack, second: ItemStack): Boolean {
        if (!ItemStack.isSameItem(first, second)) {
            return false
        }
        return first.damageValue == second.damageValue && ItemStack.isSameItemSameComponents(first, second)
    }

    override fun canMerge(first: ItemStack, second: ItemStack, stackLimit: Int?): Boolean {
        if (!canStack(first, second)) {
            return false
        }
        val realStackLimit = if (stackLimit == null) first.maxStackSize else minOf(stackLimit, first.maxStackSize)
        return first.count < realStackLimit
    }

    /**
     * Merge second item stack into first one and returns remains
     */
    override fun inplaceMerge(first: ItemStack, second: ItemStack, mergeLimit: Int?): ItemStack {
        if (!canMerge(first, second, mergeLimit)) {
            return second
        }
        val mergeSize = minOf(second.count, first.maxStackSize - first.count, mergeLimit ?: Int.MAX_VALUE)
        first.grow(mergeSize)
        second.shrink(mergeSize)
        if (second.isEmpty) {
            return ItemStack.EMPTY
        }
        return second
    }

    override fun getZero(): Int = 0

    override fun isZero(value: Int): Boolean = value == 0

    override fun biggerThanZero(value: Int): Boolean = value > 0

    override fun min(first: Int, second: Int): Int = first.coerceAtMost(second)

    override fun subtract(first: Int, second: Int): Int = first - second

    override fun add(first: Int, second: Int): Int = first + second

    fun toInventoryOrToWorld(output: ItemStack, inventory: AgnosticSink<ItemStack, Int>, outputPos: BlockPos, level: Level) {
        val rest = inventory.store(output, false)
        if (!rest.isEmpty) {
            Containers.dropItemStack(level, outputPos.x.toDouble(), outputPos.y.toDouble(), outputPos.z.toDouble(), rest)
        }
    }

    fun toInventoryOrToWorld(output: ItemStack, inventory: SlottedAgnosticStorage<ItemStack, Int>, startSlot: Int, outputPos: BlockPos, level: Level) {
        var rest = inventory.store(output, startSlot, false)
        if (!rest.isEmpty && startSlot > 0) {
            rest = inventory.store(rest, 0, startSlot - 1, false)
        }
        if (!rest.isEmpty) {
            Containers.dropItemStack(level, outputPos.x.toDouble(), outputPos.y.toDouble(), outputPos.z.toDouble(), rest)
        }
    }
}
