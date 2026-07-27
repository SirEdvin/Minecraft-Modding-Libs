package site.siredvin.broccolium.modules.data

import site.siredvin.broccolium.modules.data.api.GeneratorSink

object LibDataProviders {
    fun add(generator: GeneratorSink) {
        generator.add(::LibENLanguageProvider)
        generator.add(::LibUALanguageProvider)
    }
}
