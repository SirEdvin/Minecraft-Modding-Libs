package site.siredvin.testiarium

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries

object FabricTestiarium : ModInitializer {
    override fun onInitialize() {
        Testiarium.init()
        ServerLifecycleEvents.SERVER_STOPPING.register { Testiarium.onServerStopped() }
    }

    @JvmStatic
    fun registerTests() {
        Testiarium.loadTests({ }, { id, function -> Registry.register(BuiltInRegistries.TEST_FUNCTION, id, function) })
    }
}
