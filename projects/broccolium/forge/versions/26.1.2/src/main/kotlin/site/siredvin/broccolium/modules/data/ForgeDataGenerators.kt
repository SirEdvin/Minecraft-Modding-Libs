package site.siredvin.broccolium.modules.data

import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.data.event.GatherDataEvent
import site.siredvin.broccolium.BroccoliumCore

@EventBusSubscriber(modid = BroccoliumCore.MOD_ID)
object ForgeDataGenerators {
    @SubscribeEvent
    fun genClientData(event: GatherDataEvent.Client) {
        LibDataProviders.add(ForgeGeneratorSink(event.generator, event))
    }

    @SubscribeEvent
    fun genServerData(event: GatherDataEvent.Server) {
        LibDataProviders.add(ForgeGeneratorSink(event.generator, event))
    }
}
