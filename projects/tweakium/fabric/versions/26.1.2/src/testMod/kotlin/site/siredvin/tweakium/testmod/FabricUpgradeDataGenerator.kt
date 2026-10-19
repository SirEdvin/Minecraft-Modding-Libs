package site.siredvin.tweakium.testmod

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import site.siredvin.broccolium.modules.data.FabricGeneratorSink

class FabricUpgradeDataGenerator : DataGeneratorEntrypoint {
    override fun onInitializeDataGenerator(generator: FabricDataGenerator) {
        NativeUpgradeDataProviders.add(FabricGeneratorSink(generator.createPack(), generator.registries))
    }
}
