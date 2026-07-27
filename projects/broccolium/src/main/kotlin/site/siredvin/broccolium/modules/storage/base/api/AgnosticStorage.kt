package site.siredvin.broccolium.modules.storage.base.api

import site.siredvin.broccolium.BroccoliumCore
import site.siredvin.broccolium.modules.storage.base.StorageUtils
import java.util.function.Predicate

interface AgnosticStorage<T, L : Number> : AgnosticSink<T, L> {
    fun getContent(): Iterator<T>
    fun take(predicate: Predicate<T>, limit: L, simulate: Boolean): T

    fun moveTo(to: AgnosticSink<T, L>, limit: L, toSlot: Int = -1, takePredicate: Predicate<T>): L {
        if (movableType != null) {
            throw IllegalStateException("With movable type you should redefine this function")
        }
        if (to.movableType == null) {
            return StorageUtils.notSoNaiveMove(this, to, limit, -1, toSlot, takePredicate, operator)
        }
        if (toSlot < 0) {
            return to.moveFrom(this, limit, -1, takePredicate)
        }
        if (to !is SlottedAgnosticSink<T, L>) {
            BroccoliumCore.LOGGER.warn("To storage doesn't support slotting, so we just ignore slot here")
            return to.moveFrom(this, limit, -1, takePredicate)
        }
        return to.moveFrom(this, limit, toSlot, -1, takePredicate)
    }
}
