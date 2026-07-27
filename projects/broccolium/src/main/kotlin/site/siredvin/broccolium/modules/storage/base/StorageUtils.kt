package site.siredvin.broccolium.modules.storage.base

import site.siredvin.broccolium.BroccoliumCore
import site.siredvin.broccolium.modules.storage.base.api.AgnosticSink
import site.siredvin.broccolium.modules.storage.base.api.AgnosticStorage
import site.siredvin.broccolium.modules.storage.base.api.SlottedAgnosticStorage
import site.siredvin.broccolium.modules.storage.base.api.SomethingOperator
import java.util.function.Predicate

object StorageUtils {
    val ALWAYS: Predicate<out Any> = Predicate { true }

    fun <T, L : Number> singularMove(from: AgnosticStorage<T, L>, to: AgnosticSink<T, L>, slidingLimit: L, fromSlot: Int = -1, toSlot: Int = -1, takePredicate: Predicate<T>, operator: SomethingOperator<T, L>, simulate: Boolean): L {
        val stack = if (fromSlot < 0) {
            from.take(takePredicate, slidingLimit, simulate)
        } else {
            if (from !is SlottedAgnosticStorage<T, L>) {
                BroccoliumCore.LOGGER.warn("From storage doesn't support slotting, so we just ignoring it")
                from.take(takePredicate, slidingLimit, simulate)
            } else {
                from.take(slidingLimit, fromSlot, fromSlot, takePredicate, simulate)
            }
        }
        if (operator.isEmpty(stack)) {
            return operator.getZero()
        }

        val stackCount = operator.getSize(stack)

        // Move item to
        val remainder = if (toSlot < 0 || to !is SlottedAgnosticStorage<T, L>) {
            to.store(stack, simulate)
        } else {
            to.store(stack, toSlot, toSlot, simulate)
        }

        // Calculate items moved
        val movedCount = operator.subtract(stackCount, operator.getSize(remainder))
        if (!operator.isEmpty(remainder)) {
            // Put reminder back
            val resultStack = if (fromSlot < 0 || from !is SlottedAgnosticStorage<T, L>) {
                from.store(remainder, simulate)
            } else {
                from.store(remainder, fromSlot, fromSlot, simulate)
            }
            if (!operator.isEmpty(resultStack) && !simulate) {
                BroccoliumCore.LOGGER.error("Transfer from {} to {} voided {} items ", from, to, resultStack)
            }
        }
        return movedCount
    }

    fun <T, L : Number> notSoNaiveMove(from: AgnosticStorage<T, L>, to: AgnosticSink<T, L>, limit: L, fromSlot: Int = -1, toSlot: Int = -1, takePredicate: Predicate<T>, operator: SomethingOperator<T, L>): L {
        var slidingLimit = limit
        var slidingCount = operator.getZero()
        while (operator.biggerThanZero(slidingLimit)) {
            // So, we are going to have two phases here.
            // First, we try to perform movement
            // And then we actually move
            val potentialMove = singularMove(from, to, slidingLimit, fromSlot, toSlot, takePredicate, operator, true)
            if (operator.isZero(potentialMove)) {
                return slidingCount
            }
            val movedCount = singularMove(from, to, potentialMove, fromSlot, toSlot, takePredicate, operator, false)
            slidingLimit = operator.subtract(slidingLimit, movedCount)
            slidingCount = operator.add(slidingCount, movedCount)
            if (operator.isZero(movedCount)) {
                return slidingCount
            }
        }
        return slidingCount
    }
}
