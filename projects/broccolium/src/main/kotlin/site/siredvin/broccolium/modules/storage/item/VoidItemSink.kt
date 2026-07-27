package site.siredvin.broccolium.modules.storage.item

import net.minecraft.world.item.ItemStack
import site.siredvin.broccolium.modules.storage.base.api.AgnosticSink
import site.siredvin.broccolium.modules.storage.base.api.SomethingOperator

class VoidItemSink : AgnosticSink<ItemStack, Int> {
    override fun store(stack: ItemStack, simulate: Boolean): ItemStack = ItemStack.EMPTY

    override fun setChanged() {
    }

    override val maxStackSize: Int
        get() = 64
    override val operator: SomethingOperator<ItemStack, Int>
        get() = ItemStorageUtils
}
