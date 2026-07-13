package site.siredvin.testiarium.fixture

import net.fabricmc.api.ModInitializer
import site.siredvin.testiarium.FabricTestiarium
import site.siredvin.testiarium.Testiarium

object FabricTestiariumTestMod : ModInitializer {
    override fun onInitialize() {
        Testiarium.register(StandaloneGameTests::class.java)
        FabricTestiarium.registerTests()
    }
}
