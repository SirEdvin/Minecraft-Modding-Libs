package site.siredvin.peripheralium

import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.data.event.GatherDataEvent
import site.siredvin.broccolium.modules.data.ForgeGeneratorSink
import site.siredvin.peripheralium.data.PeripheraliumDataProviders

@EventBusSubscriber(modid = PeripheraliumCore.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
object ForgePeripheraliumDataGenerators {
    @SubscribeEvent
    fun genData(event: GatherDataEvent) {
        PeripheraliumDataProviders.add(ForgeGeneratorSink(event.generator, event))
    }
}
