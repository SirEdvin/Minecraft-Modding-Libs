package site.siredvin.broccolium.testmod

// Test-only version boundary; shared conservation fixtures retain native types.
typealias GameTest = net.minecraft.gametest.framework.GameTest
typealias InventoryStorage = net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage

object FabricGameTest {
    const val EMPTY_STRUCTURE = net.fabricmc.fabric.api.gametest.v1.FabricGameTest.EMPTY_STRUCTURE
}
