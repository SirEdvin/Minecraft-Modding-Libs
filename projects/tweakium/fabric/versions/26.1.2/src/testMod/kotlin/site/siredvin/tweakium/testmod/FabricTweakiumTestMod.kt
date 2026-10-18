package site.siredvin.tweakium.testmod

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import site.siredvin.testiarium.FabricTestiarium
import site.siredvin.testiarium.Testiarium
import site.siredvin.testiarium.cct.CctComputers
import site.siredvin.testiarium.cct.CctFixtureCommands

object FabricTweakiumTestMod : ModInitializer {
    override fun onInitialize() {
        site.siredvin.broccolium.FabricBroccolium.sayHi()
        site.siredvin.tweakium.modules.FabricTweakium.sayHi()
        NativeUpgradeFixtures.register(site.siredvin.tweakium.modules.platform.FabricTweakiumPlatform)
        CctComputers.initialize()
        ServerLifecycleEvents.SERVER_STARTING.register {
            CctComputers.reset()
            CctFixtureCommands.importFiles(it)
        }
        Testiarium.register(CreativeFillerGameTests::class.java, ModuleBehaviorGameTests::class.java, UpgradeBehaviorGameTests::class.java, WorldBehaviorGameTests::class.java, RuntimeBehaviorGameTests::class.java, NativeSessionGameTests::class.java)
        Testiarium.register(NativeUpgradeGameTests::class.java)
        NativePluginFixtures.install()
        Testiarium.register(NativePluginGameTests::class.java)
        FabricInteractionFixtures.install()
        Testiarium.register(NativeInteractionGameTests::class.java)
        Testiarium.register(site.siredvin.peripheralium.testmod.PeripheraliumBehaviorGameTests::class.java)
        FabricTestiarium.registerTests()
    }
}
