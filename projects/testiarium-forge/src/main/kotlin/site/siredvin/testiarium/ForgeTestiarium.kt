package site.siredvin.testiarium

import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.RegisterGameTestsEvent
import net.neoforged.neoforge.event.server.ServerStartedEvent
import net.neoforged.neoforge.event.server.ServerStoppingEvent

@Mod(Testiarium.MOD_ID)
class ForgeTestiarium(modBus: IEventBus) {
    init {
        Testiarium.init()
        NeoForge.EVENT_BUS.addListener { event: ServerStartedEvent -> Testiarium.onServerStarted(event.server) }
        NeoForge.EVENT_BUS.addListener { _: ServerStoppingEvent -> Testiarium.onServerStopped() }
        modBus.addListener<RegisterGameTestsEvent> { Testiarium.loadTests(it::register) }
    }
}
