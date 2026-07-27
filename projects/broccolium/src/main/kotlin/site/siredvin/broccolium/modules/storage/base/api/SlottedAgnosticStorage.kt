package site.siredvin.broccolium.modules.storage.base.api

import site.siredvin.broccolium.BroccoliumCore
import site.siredvin.broccolium.modules.storage.base.SlottedStorageIterator
import site.siredvin.broccolium.modules.storage.base.StorageUtils
import java.util.function.Predicate

interface SlottedAgnosticStorage<T, L : Number> :
    AgnosticStorage<T, L>,
    SlottedAgnosticSink<T, L>,
    AccessibleAgnosticStorage<T, L> {
    fun take(limit: L, startSlot: Int, endSlot: Int, predicate: Predicate<T>, simulate: Boolean): T

    fun canPlace(slot: Int, item: T): Boolean

    fun getLimit(slot: Int): Long

    override fun getContent(): Iterator<T> = SlottedStorageIterator(this)

    override fun take(predicate: Predicate<T>, limit: L, simulate: Boolean): T = take(limit, 0, size - 1, predicate, simulate)

    fun moveTo(to: AgnosticSink<T, L>, limit: L, fromSlot: Int = -1, toSlot: Int = -1, takePredicate: Predicate<T>): L {
        if (movableType != null) {
            throw IllegalStateException("With movable type you should redefine this function")
        }
        if (to.movableType == null) {
            return StorageUtils.notSoNaiveMove(this, to, limit, fromSlot, toSlot, takePredicate, operator)
        }
        if (toSlot < 0) {
            return to.moveFrom(this, limit, fromSlot, takePredicate)
        }
        if (to !is SlottedAgnosticSink) {
            BroccoliumCore.LOGGER.warn("To storage doesn't support slotting, so we just ignore slot here")
            return to.moveFrom(this, limit, fromSlot, takePredicate)
        }
        return to.moveFrom(this, limit, toSlot, fromSlot, takePredicate)
    }

    override fun moveTo(to: AgnosticSink<T, L>, limit: L, toSlot: Int, takePredicate: Predicate<T>): L = moveTo(to, limit, -1, toSlot, takePredicate)
}
