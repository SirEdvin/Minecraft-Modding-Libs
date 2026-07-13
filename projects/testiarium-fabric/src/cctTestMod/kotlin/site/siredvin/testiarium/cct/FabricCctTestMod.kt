package site.siredvin.testiarium.cct

import dan200.computercraft.api.peripheral.IPeripheral
import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.gametest.framework.GameTest
import net.minecraft.gametest.framework.GameTestHelper
import site.siredvin.testiarium.FabricTestiarium
import site.siredvin.testiarium.Testiarium

object FabricCctTestMod : ModInitializer {
    override fun onInitialize() {
        if (!FabricLoader.getInstance().isModLoaded("computercraft")) return
        Testiarium.register(PeripheralGameTests::class.java)
        FabricTestiarium.registerTests()
    }
}

class PeripheralGameTests {
    @GameTest(template = "empty")
    fun publicPeripheralApi(helper: GameTestHelper) {
        check(IPeripheral::class.java.isInterface)
        helper.succeed()
    }
}
