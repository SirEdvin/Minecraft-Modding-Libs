package site.siredvin.tweakium.testmod;

import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import site.siredvin.testiarium.Testiarium;
import site.siredvin.testiarium.cct.CctComputers;
import site.siredvin.testiarium.cct.CctFixtureCommands;

@Mod("tweakium_testmod")
public final class ForgeTweakiumTestMod {
    public ForgeTweakiumTestMod() {
        site.siredvin.tweakium.ForgeTweakium.INSTANCE.sayHi();
        NativeUpgradeFixtures.register(site.siredvin.tweakium.modules.platform.ForgeTweakiumPlatform.INSTANCE);
        CctComputers.INSTANCE.initialize();
        Testiarium.register(NativeUpgradeGameTests.class);
        ForgeInteractionFixtures.install();
        Testiarium.register(NativeInteractionGameTests.class);
        NeoForge.EVENT_BUS.addListener((ServerStartingEvent event) -> {
            CctComputers.INSTANCE.reset();
            CctFixtureCommands.INSTANCE.importFiles(event.getServer());
        });
        Testiarium.register(CreativeFillerGameTests.class, ModuleBehaviorGameTests.class,
            UpgradeBehaviorGameTests.class, WorldBehaviorGameTests.class, RuntimeBehaviorGameTests.class, NativeSessionGameTests.class);
    }
}
