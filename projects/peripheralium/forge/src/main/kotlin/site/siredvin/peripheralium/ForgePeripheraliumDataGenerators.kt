package site.siredvin.peripheralium

import net.minecraftforge.data.event.GatherDataEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import site.siredvin.broccolium.modules.data.ForgeGeneratorSink
import site.siredvin.peripheralium.data.PeripheraliumDataProviders

@Mod.EventBusSubscriber(modid = PeripheraliumCore.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
object ForgePeripheraliumDataGenerators {
    @SubscribeEvent
    fun genData(event: GatherDataEvent) {
        val generator = event.generator
        PeripheraliumDataProviders.add(
            ForgeGeneratorSink(
                generator.getVanillaPack(true),
                event.existingFileHelper,
                event.lookupProvider,
            ),
        )
    }
}
