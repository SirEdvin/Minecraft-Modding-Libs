package site.siredvin.broccolium.testmod

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage
import net.minecraft.core.BlockPos
import net.minecraft.gametest.framework.GameTest
import net.minecraft.gametest.framework.GameTestHelper
import net.minecraft.world.SimpleContainer
import site.siredvin.broccolium.integrations.team_reborn_energy.Integration
import site.siredvin.broccolium.modules.storage.item.ContainerWrapper
import site.siredvin.broccolium.modules.storage.item.FabricSlottedStorageWrapper
import site.siredvin.broccolium.modules.storage.item.SlottedAgnosticItemStorageWrapper
import site.siredvin.testiarium.api.TestGroup
import site.siredvin.testiarium.api.immediate

@TestGroup("broccolium_integrations")
class EnergyIntegrationGameTests {
    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    fun absentEnergyProvidersReturnNullWithoutChangingItems(helper: GameTestHelper) = helper.immediate {
        val original = bridgeStack(5, "energy-lookup")
        val container = SimpleContainer(original.copy())
        val native = FabricSlottedStorageWrapper(InventoryStorage.of(container, null))
        val agnostic = ContainerWrapper(container)
        try {
            check(Integration.extractEnergyStorage(helper.level, native, 0) == null)
            check(Integration.extractEnergyStorage(helper.level, agnostic, 0) == null)
            check(Integration.extractEnergyStorage(helper.level, helper.absolutePos(BlockPos(1, 1, 1)), null, null) == null)
            assertStack(container.getItem(0), original, "energy lookup must not mutate its item context")
            check(!SlottedAgnosticItemStorageWrapper.WRAPPERS.containsKey(native))
        } finally {
            SlottedAgnosticItemStorageWrapper.WRAPPERS.remove(agnostic)
        }
    }
}
