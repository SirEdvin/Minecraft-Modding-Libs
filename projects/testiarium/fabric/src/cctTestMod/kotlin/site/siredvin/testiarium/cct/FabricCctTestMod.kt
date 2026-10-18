package site.siredvin.testiarium.cct

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.loader.api.FabricLoader

object FabricCctTestMod : ModInitializer {
    override fun onInitialize() {
        if (!FabricLoader.getInstance().isModLoaded("computercraft")) return
        CctComputers.initialize()
        CommandRegistrationCallback.EVENT.register { dispatcher, context, _ -> CctFixtureCommands.register(dispatcher, context) }
        ServerLifecycleEvents.SERVER_STARTING.register {
            CctComputers.reset()
            CctFixtureCommands.importFiles(it)
        }
        // The owning testmod registers its classes before the one-shot scan.
    }
}
