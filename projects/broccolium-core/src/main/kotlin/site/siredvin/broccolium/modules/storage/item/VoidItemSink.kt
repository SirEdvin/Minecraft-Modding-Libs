package site.siredvin.broccolium.modules.storage.item

import net.minecraft.world.item.ItemStack
import site.siredvin.broccolium.modules.storage.item.api.AgnosticItemSink

class VoidItemSink : AgnosticItemSink {
    override fun storeItem(stack: ItemStack): ItemStack = ItemStack.EMPTY

    override fun setChanged() {
    }
}
