package site.siredvin.broccolium.test

import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction
import site.siredvin.broccolium.modules.platform.PlatformToolkit
import site.siredvin.broccolium.modules.storage.fluid.AgnosticFluidStack
import site.siredvin.broccolium.modules.storage.fluid.FabricAgnosticFluidStorage
import site.siredvin.broccolium.modules.storage.fluid.api.AgnosticFluidStorage
import site.siredvin.broccolium.modules.storage.fluid.toVariant
import site.siredvin.broccolium.test.storage.DummyFluidStorage

@WithMinecraft
internal class FabricFluidStorageTests : FluidStorageTests() {

    override fun createStorage(fluids: List<AgnosticFluidStack>, secondary: Boolean): AgnosticFluidStorage {
        return FabricAgnosticFluidStorage(
            CombinedStorage(
                fluids.map { stack ->
                    val storage =
                        SingleFluidStorage.withFixedCapacity(1000L * PlatformToolkit.get().fluidCompactDivider.toLong()) {}
                    if (!stack.isEmpty) {
                        Transaction.openOuter().use {
                            storage.insert(stack.toVariant(), stack.platformAmount.toLong(), it)
                            it.commit()
                        }
                    }
                    return@map storage
                },
            ),
        )
    }
}

@WithMinecraft
internal class FabricDummyFluidStorageTests : FluidStorageTests() {

    override fun createStorage(fluids: List<AgnosticFluidStack>, secondary: Boolean): AgnosticFluidStorage {
        if (secondary) {
            return DummyFluidStorage(fluids.size, fluids)
        }
        return FabricAgnosticFluidStorage(
            CombinedStorage(
                fluids.map { stack ->
                    val storage =
                        SingleFluidStorage.withFixedCapacity(1000L * PlatformToolkit.get().fluidCompactDivider.toLong()) {}
                    if (!stack.isEmpty) {
                        Transaction.openOuter().use {
                            storage.insert(stack.toVariant(), stack.platformAmount.toLong(), it)
                            it.commit()
                        }
                    }
                    return@map storage
                },
            ),
        )
    }
}

@WithMinecraft
internal class FabricReverseDummyFluidStorageTests : FluidStorageTests() {

    override fun createStorage(fluids: List<AgnosticFluidStack>, secondary: Boolean): AgnosticFluidStorage {
        if (!secondary) {
            return DummyFluidStorage(fluids.size, fluids)
        }
        return FabricAgnosticFluidStorage(
            CombinedStorage(
                fluids.map { stack ->
                    val storage =
                        SingleFluidStorage.withFixedCapacity(1000L * PlatformToolkit.get().fluidCompactDivider.toLong()) {}
                    if (!stack.isEmpty) {
                        Transaction.openOuter().use {
                            storage.insert(stack.toVariant(), stack.platformAmount.toLong(), it)
                            it.commit()
                        }
                    }
                    return@map storage
                },
            ),
        )
    }
}
