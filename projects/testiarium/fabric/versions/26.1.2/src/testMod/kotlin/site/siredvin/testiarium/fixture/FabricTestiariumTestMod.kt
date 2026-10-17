package site.siredvin.testiarium.fixture

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.loader.api.FabricLoader
import site.siredvin.testiarium.FabricTestiarium
import site.siredvin.testiarium.Testiarium

object FabricTestiariumTestMod : ModInitializer {
    override fun onInitialize() {
        if (!java.lang.Boolean.getBoolean("testiarium.fixture.consumer")) {
            Testiarium.register(StandaloneGameTests::class.java)
            // Consumer and CCT entrypoints own their single static-registry scan.
            if (!FabricLoader.getInstance().isModLoaded("testiarium_cct_testmod")) FabricTestiarium.registerTests()
        }
        CommandRegistrationCallback.EVENT.register { dispatcher, context, _ -> FixtureCommands.register(dispatcher, context) }
    }
}
