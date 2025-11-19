package site.siredvin.broccolium.test.storage

import net.minecraft.world.item.ItemStack

class NoTakebackDummyStorage(maxSlots: Int, initialItems: List<ItemStack>) : DummyStorage(maxSlots, initialItems) {
    override fun store(stack: ItemStack, simulate: Boolean): ItemStack = stack
}
