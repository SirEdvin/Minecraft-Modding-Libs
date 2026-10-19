package site.siredvin.peripheralium

import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.data.event.GatherDataEvent
import site.siredvin.broccolium.modules.data.ForgeGeneratorSink
import site.siredvin.peripheralium.data.PeripheraliumDataProviders

@EventBusSubscriber(modid = PeripheraliumCore.MOD_ID)
object ForgePeripheraliumDataGenerators {
    @SubscribeEvent
    fun client(event: GatherDataEvent.Client) {
        PeripheraliumDataProviders.add(ForgeGeneratorSink(event.generator, event))
    }

    @SubscribeEvent
    fun server(event: GatherDataEvent.Server) {
        PeripheraliumDataProviders.add(ForgeGeneratorSink(event.generator, event))
    }
}
