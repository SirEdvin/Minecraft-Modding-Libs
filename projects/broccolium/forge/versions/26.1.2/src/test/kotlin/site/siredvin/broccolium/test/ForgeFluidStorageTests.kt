package site.siredvin.broccolium.test

import net.neoforged.neoforge.transfer.fluid.FluidResource
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler
import net.neoforged.neoforge.transfer.transaction.Transaction
import site.siredvin.broccolium.modules.storage.fluid.AgnosticFluidStack
import site.siredvin.broccolium.modules.storage.fluid.ForgeAgnosticFluidStorage
import site.siredvin.broccolium.modules.storage.fluid.api.AgnosticFluidStorage
import site.siredvin.broccolium.test.storage.DummyFluidStorage

private fun fluidStorage(fluids: List<AgnosticFluidStack>): AgnosticFluidStorage {
    val handler = FluidStacksResourceHandler(fluids.size, 1000)
    Transaction.openRoot().use { transaction ->
        fluids.forEachIndexed { index, stack ->
            if (!stack.isEmpty) handler.insert(index, FluidResource.of(stack.fluid, stack.components), stack.amount.toInt(), transaction)
        }
        transaction.commit()
    }
    return ForgeAgnosticFluidStorage(handler)
}

@WithNeoForgeRegistries
internal class ForgeFluidStorageTests : FluidStorageTests() {
    override fun createStorage(fluids: List<AgnosticFluidStack>, secondary: Boolean): AgnosticFluidStorage = fluidStorage(fluids)
}

@WithNeoForgeRegistries
internal class ForgeDummyFluidStorageTests : FluidStorageTests() {
    override fun createStorage(fluids: List<AgnosticFluidStack>, secondary: Boolean): AgnosticFluidStorage = if (secondary) DummyFluidStorage(fluids.size, fluids) else fluidStorage(fluids)
}
