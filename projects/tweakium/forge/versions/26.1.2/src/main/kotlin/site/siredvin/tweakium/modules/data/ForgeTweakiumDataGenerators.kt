package site.siredvin.tweakium.modules.data

import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.data.event.GatherDataEvent
import site.siredvin.broccolium.modules.data.ForgeGeneratorSink
import site.siredvin.tweakium.TweakiumCore

@EventBusSubscriber(modid = TweakiumCore.MOD_ID)
object ForgeTweakiumDataGenerators {
    @SubscribeEvent
    fun client(event: GatherDataEvent.Client) {
        TweakiumDataProviders.add(ForgeGeneratorSink(event.generator, event))
    }

    @SubscribeEvent
    fun server(event: GatherDataEvent.Server) {
        TweakiumDataProviders.add(ForgeGeneratorSink(event.generator, event))
    }
}
