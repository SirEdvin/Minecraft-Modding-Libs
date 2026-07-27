package site.siredvin.peripheralium.data

import site.siredvin.broccolium.modules.data.api.GeneratorSink

object PeripheraliumDataProviders {
    fun add(generator: GeneratorSink) {
        generator.add(::`PeripheraliumRecipeProvider`)
        generator.lootTable(PeripheraliumLootTableProvider.getTables())
        generator.models(PeripheraliumBlockModelProvider::addModels, PeripheraliumItemModelProvider::addModels)
        generator.add(::PeripheraliumENLanguageProvider)
        generator.add(::PeripheraliumUALanguageProvider)
    }
}
