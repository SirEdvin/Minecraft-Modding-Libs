package site.siredvin.broccolium.modules.data

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator

class LibDataGenerator : DataGeneratorEntrypoint {

    override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
        LibDataProviders.add(FabricGeneratorSink(fabricDataGenerator.createPack(), fabricDataGenerator.registries))
    }
}
