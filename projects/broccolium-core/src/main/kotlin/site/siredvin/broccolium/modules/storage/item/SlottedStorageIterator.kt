package site.siredvin.broccolium.modules.storage.item

import net.minecraft.world.item.ItemStack
import site.siredvin.broccolium.modules.storage.item.api.SlottedAgnosticItemStorage

class SlottedStorageIterator(private val storage: SlottedAgnosticItemStorage) : Iterator<ItemStack> {
    private var currentIndex = 0
    override fun hasNext(): Boolean = currentIndex < storage.size

    override fun next(): ItemStack {
        val oldIndex = currentIndex
        currentIndex += 1
        return storage.getItem(oldIndex)
    }
}
