package site.siredvin.tweakium.modules.data

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import site.siredvin.broccolium.modules.data.FabricGeneratorSink

class TweakiumDataGenerator : DataGeneratorEntrypoint {
    override fun onInitializeDataGenerator(generator: FabricDataGenerator) {
        TweakiumDataProviders.add(FabricGeneratorSink(generator.createPack(), generator.registries))
    }
}
