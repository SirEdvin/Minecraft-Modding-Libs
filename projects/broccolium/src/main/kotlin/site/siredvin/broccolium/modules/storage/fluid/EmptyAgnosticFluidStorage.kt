package site.siredvin.broccolium.modules.storage.fluid

import site.siredvin.broccolium.modules.storage.base.api.SomethingOperator
import site.siredvin.broccolium.modules.storage.fluid.api.AgnosticFluidStorage
import java.util.function.Predicate

object EmptyAgnosticFluidStorage : AgnosticFluidStorage {
    override fun getContent(): Iterator<AgnosticFluidStack> = emptyList<AgnosticFluidStack>().iterator()
    override fun getCapacities(): List<Double> = emptyList()

    override fun take(predicate: Predicate<AgnosticFluidStack>, limit: Double, simulate: Boolean): AgnosticFluidStack = AgnosticFluidStack.EMPTY

    override fun store(stack: AgnosticFluidStack, simulate: Boolean): AgnosticFluidStack = stack

    override fun setChanged() {
    }

    override val maxStackSize: Double
        get() = 0.0
    override val operator: SomethingOperator<AgnosticFluidStack, Double>
        get() = FluidStorageUtils
}
