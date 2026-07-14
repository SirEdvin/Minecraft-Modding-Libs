package site.siredvin.broccolium.testmod

import net.fabricmc.api.ModInitializer
import net.minecraft.gametest.framework.GameTest
import net.minecraft.gametest.framework.GameTestHelper
import site.siredvin.testiarium.FabricTestiarium
import site.siredvin.testiarium.Testiarium

object BroccoliumTestiariumFabricTestMod : ModInitializer {
    override fun onInitialize() {
        Testiarium.register(GameTests::class.java)
        FabricTestiarium.registerTests()
    }
}

class GameTests {
    @GameTest(template = "empty")
    fun loads(helper: GameTestHelper) {
        helper.succeed()
    }
}
