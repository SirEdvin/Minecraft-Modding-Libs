package site.siredvin.broccolium.modules.storage.base.api

import site.siredvin.broccolium.BroccoliumCore
import site.siredvin.broccolium.modules.storage.base.StorageUtils
import java.util.function.Predicate

interface SlottedAgnosticSink<T, L : Number> : AgnosticSink<T, L> {
    val size: Int

    fun store(stack: T, startSlot: Int, endSlot: Int, simulate: Boolean): T

    fun store(stack: T, startSlot: Int, simulate: Boolean): T = store(stack, startSlot, size - 1, simulate)
    override fun store(stack: T, simulate: Boolean): T = store(stack, 0, size - 1, simulate)

    fun moveFrom(from: AgnosticStorage<T, L>, limit: L, toSlot: Int = -1, fromSlot: Int = -1, takePredicate: Predicate<T>): L {
        if (movableType != null) {
            throw IllegalStateException("With movable type you should redefine this function")
        }
        if (from.movableType == null) {
            return StorageUtils.notSoNaiveMove(
                from,
                this,
                limit,
                fromSlot,
                toSlot,
                takePredicate,
                operator,
            )
        }
        if (fromSlot < 0) {
            return from.moveTo(this, limit, toSlot, takePredicate)
        }
        if (from !is SlottedAgnosticStorage) {
            BroccoliumCore.LOGGER.warn("From storage doesn't support slotting, so we just ignore slot here")
            return from.moveTo(this, limit, toSlot, takePredicate)
        }
        return from.moveTo(this, limit, fromSlot, toSlot, takePredicate)
    }

    override fun moveFrom(
        from: AgnosticStorage<T, L>,
        limit: L,
        fromSlot: Int,
        takePredicate: Predicate<T>,
    ): L = moveFrom(from, limit, -1, fromSlot, takePredicate)
}
