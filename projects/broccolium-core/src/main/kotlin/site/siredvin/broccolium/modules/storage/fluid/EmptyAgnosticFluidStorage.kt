package site.siredvin.broccolium.modules.storage.fluid

import site.siredvin.broccolium.modules.storage.fluid.api.AgnosticFluidStorage
import java.util.function.Predicate

object EmptyAgnosticFluidStorage : AgnosticFluidStorage {
    override fun getFluids(): Iterator<AgnosticFluidStack> = emptyList<AgnosticFluidStack>().iterator()

    override fun takeFluid(predicate: Predicate<AgnosticFluidStack>, limit: Long): AgnosticFluidStack = AgnosticFluidStack.EMPTY

    override fun storeFluid(stack: AgnosticFluidStack): AgnosticFluidStack = stack

    override fun setChanged() {
    }
}
