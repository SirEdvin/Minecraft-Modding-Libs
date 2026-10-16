package site.siredvin.tweakium.testmod

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import site.siredvin.peripheralium.testmod.PeripheraliumBehaviorGameTests
import site.siredvin.testiarium.FabricTestiarium
import site.siredvin.testiarium.Testiarium
import site.siredvin.testiarium.cct.CctComputers
import site.siredvin.testiarium.cct.CctFixtureCommands

object FabricTweakiumTestMod : ModInitializer {
    override fun onInitialize() {
        CctComputers.initialize()
        ServerLifecycleEvents.SERVER_STARTING.register {
            CctComputers.reset()
            CctFixtureCommands.importFiles(it)
        }
        Testiarium.register(CreativeFillerGameTests::class.java, ModuleBehaviorGameTests::class.java, UpgradeBehaviorGameTests::class.java, WorldBehaviorGameTests::class.java)
        Testiarium.register(PeripheraliumBehaviorGameTests::class.java)
        Testiarium.register(RuntimeBehaviorGameTests::class.java)
        FabricTestiarium.registerTests()
    }
}
