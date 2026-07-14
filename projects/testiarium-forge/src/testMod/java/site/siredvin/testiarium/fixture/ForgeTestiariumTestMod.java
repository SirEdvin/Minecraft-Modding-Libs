package site.siredvin.testiarium.fixture;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import site.siredvin.testiarium.Testiarium;
import site.siredvin.testiarium.fixture.client.ForgeClientTestHooks;

@Mod("testiarium_testmod")
public final class ForgeTestiariumTestMod {
    public ForgeTestiariumTestMod() {
        Testiarium.register(StandaloneGameTests.class);
        if (FMLEnvironment.dist == Dist.CLIENT) ForgeClientTestHooks.register();
        NeoForge.EVENT_BUS.addListener((RegisterCommandsEvent event) -> FixtureCommands.INSTANCE.register(event.getDispatcher(), event.getBuildContext()));
    }
}
