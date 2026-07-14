package site.siredvin.testiarium.cct;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import site.siredvin.testiarium.ForgeTestiarium;

@Mod("testiarium_cct_testmod")
public final class ForgeCctTestMod {
    public ForgeCctTestMod() {
        if (!ModList.get().isLoaded("computercraft")) return;
        CctComputers.INSTANCE.initialize();
        MinecraftForge.EVENT_BUS.addListener((RegisterCommandsEvent event) -> CctFixtureCommands.INSTANCE.register(event.getDispatcher(), event.getBuildContext()));
        MinecraftForge.EVENT_BUS.addListener((ServerStartingEvent event) -> {
            CctComputers.INSTANCE.reset();
            CctFixtureCommands.INSTANCE.importFiles(event.getServer());
        });
        ForgeTestiarium.registerTests();
    }
}
