package site.siredvin.broccolium.modules.storage.fluid.api

import site.siredvin.broccolium.modules.storage.fluid.AgnosticFluidStack
import site.siredvin.broccolium.modules.storage.fluid.FluidStorageUtils
import java.util.function.Predicate

interface AgnosticFluidStorage : AgnosticFluidSink {
    fun getFluids(): Iterator<AgnosticFluidStack>
    fun takeFluid(predicate: Predicate<AgnosticFluidStack>, limit: Long): AgnosticFluidStack

    fun moveTo(to: AgnosticFluidSink, limit: Long, takePredicate: Predicate<AgnosticFluidStack>): Long {
        if (movableType != null) {
            throw IllegalStateException("With movable type you should redefine this function")
        }
        if (to.movableType == null) {
            return FluidStorageUtils.naiveMove(this, to, limit, takePredicate)
        }
        return to.moveFrom(this, limit, takePredicate)
    }
}
