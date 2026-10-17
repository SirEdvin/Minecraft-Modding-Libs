package site.siredvin.broccolium.testmod

// Test-only version boundary; no aliases or fixtures enter production artifacts.
typealias GameTest = site.siredvin.testiarium.api.GameTest
typealias InventoryStorage = net.fabricmc.fabric.api.transfer.v1.item.ContainerStorage

object FabricGameTest {
    const val EMPTY_STRUCTURE = "empty"
}
