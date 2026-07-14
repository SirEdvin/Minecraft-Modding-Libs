package site.siredvin.testiarium

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.minecraft.gametest.framework.GameTestRegistry

object FabricTestiarium : ModInitializer {
    private var testsRegistered = false

    override fun onInitialize() {
        Testiarium.init()
        ServerLifecycleEvents.SERVER_STARTED.register(Testiarium::onServerStarted)
        ServerLifecycleEvents.SERVER_STOPPING.register { Testiarium.onServerStopped() }
    }

    @JvmStatic
    fun registerTests() {
        if (testsRegistered) return
        testsRegistered = true
        Testiarium.loadTests(GameTestRegistry::register)
    }
}
