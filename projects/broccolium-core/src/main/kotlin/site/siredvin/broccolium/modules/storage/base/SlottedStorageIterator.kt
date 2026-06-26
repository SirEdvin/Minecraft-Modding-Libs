package site.siredvin.broccolium.modules.storage.base

import site.siredvin.broccolium.modules.storage.base.api.SlottedAgnosticStorage

class SlottedStorageIterator<T, L : Number>(private val storage: SlottedAgnosticStorage<T, L>) : Iterator<T> {
    private var currentIndex = 0
    override fun hasNext(): Boolean = currentIndex < storage.size

    override fun next(): T {
        val oldIndex = currentIndex
        currentIndex += 1
        return storage.get(oldIndex)
    }
}
