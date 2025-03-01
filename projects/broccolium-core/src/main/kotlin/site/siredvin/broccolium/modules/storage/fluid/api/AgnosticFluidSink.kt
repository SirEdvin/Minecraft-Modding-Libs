package site.siredvin.broccolium.modules.storage.fluid.api

import site.siredvin.broccolium.modules.storage.fluid.AgnosticFluidStack
import site.siredvin.broccolium.modules.storage.fluid.FluidStorageUtils
import java.util.function.Predicate

interface AgnosticFluidSink {
    fun moveFrom(from: AgnosticFluidStorage, limit: Long, takePredicate: Predicate<AgnosticFluidStack>): Long {
        if (movableType != null) {
            throw IllegalStateException("With movable type you should redefine this function")
        }
        if (from.movableType == null) {
            return FluidStorageUtils.naiveMove(from, this, limit, takePredicate)
        }
        return from.moveTo(this, limit, takePredicate)
    }
    fun storeFluid(stack: AgnosticFluidStack): AgnosticFluidStack
    fun setChanged()

    val movableType: String?
        get() = null
}
