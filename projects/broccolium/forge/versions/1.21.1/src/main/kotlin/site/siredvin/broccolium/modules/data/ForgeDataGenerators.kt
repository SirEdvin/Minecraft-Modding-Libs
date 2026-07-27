package site.siredvin.broccolium.modules.data

import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.data.event.GatherDataEvent
import site.siredvin.broccolium.BroccoliumCore

@EventBusSubscriber(modid = BroccoliumCore.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
object ForgeDataGenerators {
    @SubscribeEvent
    fun genData(event: GatherDataEvent) {
        LibDataProviders.add(ForgeGeneratorSink(event.generator, event))
    }
}
