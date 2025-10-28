package site.siredvin.broccolium.modules.storage.fluid.api

import site.siredvin.broccolium.modules.storage.fluid.AgnosticFluidStack
import site.siredvin.broccolium.modules.storage.fluid.FluidStorageUtils
import java.util.function.Predicate

interface AgnosticFluidStorage : AgnosticFluidSink {
    fun getFluids(): Iterator<AgnosticFluidStack>
    fun getCapacities(): List<Double>
    fun takeFluid(predicate: Predicate<AgnosticFluidStack>, limit: Double): AgnosticFluidStack

    fun moveTo(to: AgnosticFluidSink, limit: Double, takePredicate: Predicate<AgnosticFluidStack>): Double {
        if (movableType != null) {
            throw IllegalStateException("With movable type you should redefine this function")
        }
        if (to.movableType == null) {
            return FluidStorageUtils.naiveMove(this, to, limit, takePredicate)
        }
        return to.moveFrom(this, limit, takePredicate)
    }
}
