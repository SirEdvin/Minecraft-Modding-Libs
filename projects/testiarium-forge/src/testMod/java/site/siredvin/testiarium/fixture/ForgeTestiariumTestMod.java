package site.siredvin.testiarium.fixture;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import site.siredvin.testiarium.ForgeTestiarium;
import site.siredvin.testiarium.Testiarium;
import site.siredvin.testiarium.fixture.client.ForgeClientTestHooks;

@Mod("testiarium_testmod")
public final class ForgeTestiariumTestMod {
    public ForgeTestiariumTestMod() {
        Testiarium.register(StandaloneGameTests.class);
        ForgeTestiarium.registerTests();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ForgeClientTestHooks::register);
        MinecraftForge.EVENT_BUS.addListener((RegisterCommandsEvent event) -> FixtureCommands.INSTANCE.register(event.getDispatcher(), event.getBuildContext()));
    }
}
