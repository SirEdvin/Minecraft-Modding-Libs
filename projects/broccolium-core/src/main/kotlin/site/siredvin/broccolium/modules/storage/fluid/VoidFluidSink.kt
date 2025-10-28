package site.siredvin.broccolium.modules.storage.fluid

import site.siredvin.broccolium.modules.storage.fluid.api.AgnosticFluidSink

class VoidFluidSink : AgnosticFluidSink {
    override fun storeFluid(stack: AgnosticFluidStack): AgnosticFluidStack = AgnosticFluidStack.EMPTY

    override fun setChanged() {
    }
}
