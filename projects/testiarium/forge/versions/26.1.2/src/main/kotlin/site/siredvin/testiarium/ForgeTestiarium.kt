package site.siredvin.testiarium

import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.RegisterGameTestsEvent
import net.neoforged.neoforge.event.server.ServerStartedEvent
import net.neoforged.neoforge.event.server.ServerStoppingEvent
import net.neoforged.neoforge.registries.RegisterEvent
import net.minecraft.core.registries.Registries

@Mod(Testiarium.MOD_ID)
class ForgeTestiarium(modBus: IEventBus) {
    init {
        Testiarium.init()
        NeoForge.EVENT_BUS.addListener { event: ServerStartedEvent -> Testiarium.onServerStarted(event.server) }
        NeoForge.EVENT_BUS.addListener { _: ServerStoppingEvent -> Testiarium.onServerStopped() }
        modBus.addListener<RegisterEvent> { event ->
            event.register(Registries.TEST_FUNCTION) { registry ->
                Testiarium.loadTests({ }, { id, function -> registry.register(id, function) })
            }
        }
        modBus.addListener<RegisterGameTestsEvent> { event ->
            Testiarium.registerInstances({ id, environment -> event.registerEnvironment(id, environment) }, event::registerTest)
        }
    }
}
