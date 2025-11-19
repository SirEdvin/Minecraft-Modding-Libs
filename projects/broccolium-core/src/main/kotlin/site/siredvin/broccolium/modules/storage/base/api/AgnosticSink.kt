package site.siredvin.broccolium.modules.storage.base.api

import site.siredvin.broccolium.BroccoliumCore
import site.siredvin.broccolium.modules.storage.base.StorageUtils
import java.util.function.Predicate

interface AgnosticSink<T, L : Number> {
    fun moveFrom(from: AgnosticStorage<T, L>, limit: L, fromSlot: Int = -1, takePredicate: Predicate<T>): L {
        if (movableType != null) {
            throw IllegalStateException("With movable type you should redefine this function")
        }
        if (from.movableType == null) {
            return StorageUtils.notSoNaiveMove(from, this, limit, fromSlot, -1, takePredicate, operator)
        }
        if (fromSlot < 0) {
            return from.moveTo(this, limit, -1, takePredicate)
        }
        if (from !is SlottedAgnosticStorage<T, L>) {
            BroccoliumCore.LOGGER.warn("From storage doesn't support slotting, so we just ignore slot here")
            return from.moveTo(this, limit, -1, takePredicate)
        }
        return from.moveTo(this, limit, fromSlot, -1, takePredicate)
    }
    fun store(stack: T, simulate: Boolean): T
    fun setChanged()

    val maxStackSize: L
    val operator: SomethingOperator<T, L>
    val movableType: String?
        get() = null
}
