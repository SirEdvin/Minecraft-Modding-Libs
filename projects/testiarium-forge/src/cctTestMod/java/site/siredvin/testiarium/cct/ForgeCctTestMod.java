package site.siredvin.testiarium.cct;

import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod("testiarium_cct_testmod")
public final class ForgeCctTestMod {
    public ForgeCctTestMod() {
        if (!ModList.get().isLoaded("computercraft")) return;
        CctComputers.INSTANCE.initialize();
        NeoForge.EVENT_BUS.addListener((RegisterCommandsEvent event) -> CctFixtureCommands.INSTANCE.register(event.getDispatcher(), event.getBuildContext()));
        NeoForge.EVENT_BUS.addListener((ServerStartingEvent event) -> {
            CctComputers.INSTANCE.reset();
            CctFixtureCommands.INSTANCE.importFiles(event.getServer());
        });
    }
}
