package site.siredvin.testiarium

import net.fabricmc.api.ModInitializer
import net.minecraft.gametest.framework.GameTestRegistry

object FabricTestiarium : ModInitializer {
    override fun onInitialize() {
        Testiarium.init()
    }

    @JvmStatic
    fun registerTests() {
        Testiarium.loadTests(GameTestRegistry::register)
    }
}
