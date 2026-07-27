package site.siredvin.broccolium.test

import site.siredvin.broccolium.modules.storage.fluid.AgnosticFluidStack
import site.siredvin.broccolium.modules.storage.fluid.api.AgnosticFluidStorage
import site.siredvin.broccolium.test.storage.DummyFluidStorage

@WithMinecraft
internal class DummyFluidStorageTests : FluidStorageTests() {
    override fun createStorage(fluids: List<AgnosticFluidStack>, secondary: Boolean): AgnosticFluidStorage = DummyFluidStorage(fluids.size, fluids)
}
