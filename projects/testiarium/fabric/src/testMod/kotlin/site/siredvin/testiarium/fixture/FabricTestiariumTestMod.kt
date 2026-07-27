package site.siredvin.testiarium.fixture

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import site.siredvin.testiarium.FabricTestiarium
import site.siredvin.testiarium.Testiarium

object FabricTestiariumTestMod : ModInitializer {
    override fun onInitialize() {
        Testiarium.register(StandaloneGameTests::class.java)
        FabricTestiarium.registerTests()
        CommandRegistrationCallback.EVENT.register { dispatcher, context, _ -> FixtureCommands.register(dispatcher, context) }
    }
}
