package site.siredvin.testiarium

import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.server.ServerStartedEvent
import net.minecraftforge.event.server.ServerStoppingEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext

@Mod(Testiarium.MOD_ID)
object ForgeTestiarium {
    private var testsRegistered = false

    init {
        Testiarium.init()
        MinecraftForge.EVENT_BUS.addListener { event: ServerStartedEvent -> Testiarium.onServerStarted(event.server) }
        MinecraftForge.EVENT_BUS.addListener { _: ServerStoppingEvent -> Testiarium.onServerStopped() }
    }

    @JvmStatic
    fun registerTests() {
        if (testsRegistered) return
        testsRegistered = true
        FMLJavaModLoadingContext.get().modEventBus.addListener { event: net.minecraftforge.event.RegisterGameTestsEvent ->
            Testiarium.loadTests(event::register)
        }
    }
}
