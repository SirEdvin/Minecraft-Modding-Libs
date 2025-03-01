package site.siredvin.periparium

import site.siredvin.peripheralium.data.blocks.GeneratorSink

object LibDataProviders {
    fun add(generator: GeneratorSink) {
        generator.add(::LibRecipeProvider)
        generator.lootTable(LibLootTableProvider.getTables())
        generator.models(LibBlockModelProvider::addModels, LibItemModelProvider::addModels)
        generator.add(::LibENLanguageProvider)
        generator.add(::LibUALanguageProvider)
    }
}
