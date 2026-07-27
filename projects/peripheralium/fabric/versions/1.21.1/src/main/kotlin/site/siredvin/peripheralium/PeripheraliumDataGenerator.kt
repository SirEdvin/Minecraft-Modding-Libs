package site.siredvin.peripheralium

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import site.siredvin.broccolium.modules.data.FabricGeneratorSink
import site.siredvin.peripheralium.data.PeripheraliumDataProviders

class PeripheraliumDataGenerator : DataGeneratorEntrypoint {

    override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
        PeripheraliumDataProviders.add(FabricGeneratorSink(fabricDataGenerator.createPack(), fabricDataGenerator.registries))
    }
}
