package site.siredvin.tweakium.testmod;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import site.siredvin.testiarium.ForgeTestiarium;
import site.siredvin.peripheralium.testmod.PeripheraliumBehaviorGameTests;
import site.siredvin.testiarium.Testiarium;
import site.siredvin.testiarium.cct.CctComputers;
import site.siredvin.testiarium.cct.CctFixtureCommands;

@Mod("tweakium_testmod")
public final class ForgeTweakiumTestMod {
    public ForgeTweakiumTestMod() {
        CctComputers.INSTANCE.initialize();
        MinecraftForge.EVENT_BUS.addListener((ServerStartingEvent event) -> {
            CctComputers.INSTANCE.reset();
            CctFixtureCommands.INSTANCE.importFiles(event.getServer());
        });
        Testiarium.register(CreativeFillerGameTests.class, ModuleBehaviorGameTests.class, UpgradeBehaviorGameTests.class, WorldBehaviorGameTests.class);
        Testiarium.register(PeripheraliumBehaviorGameTests.class);
        Testiarium.register(RuntimeBehaviorGameTests.class);
        ForgeTestiarium.registerTests();
    }
}
