package site.siredvin.broccolium.modules.storage.fluid

import site.siredvin.broccolium.modules.storage.base.api.AgnosticSink
import site.siredvin.broccolium.modules.storage.base.api.SomethingOperator

class VoidFluidSink : AgnosticSink<AgnosticFluidStack, Double> {
    override fun store(
        stack: AgnosticFluidStack,
        simulate: Boolean,
    ): AgnosticFluidStack = AgnosticFluidStack.EMPTY

    override fun setChanged() {
    }

    override val maxStackSize: Double
        get() = Double.MAX_VALUE
    override val operator: SomethingOperator<AgnosticFluidStack, Double>
        get() = FluidStorageUtils
}
