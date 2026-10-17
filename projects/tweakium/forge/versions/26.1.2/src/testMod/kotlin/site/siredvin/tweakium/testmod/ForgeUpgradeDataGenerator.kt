package site.siredvin.tweakium.testmod

import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.data.event.GatherDataEvent
import site.siredvin.broccolium.modules.data.ForgeGeneratorSink

@EventBusSubscriber(modid = "tweakium_testmod")
object ForgeUpgradeDataGenerator {
    @SubscribeEvent
    @JvmStatic
    fun client(event: GatherDataEvent.Client) {
        NativeUpgradeDataProviders.add(ForgeGeneratorSink(event.generator, event))
    }

    @SubscribeEvent
    @JvmStatic
    fun server(event: GatherDataEvent.Server) {
        NativeUpgradeDataProviders.add(ForgeGeneratorSink(event.generator, event))
    }
}
