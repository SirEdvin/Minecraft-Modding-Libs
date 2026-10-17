package site.siredvin.tweakium.modules.data

import net.minecraft.data.DataProvider
import net.minecraft.data.PackOutput
import site.siredvin.broccolium.modules.data.api.GeneratorSink
import site.siredvin.tweakium.TweakiumCore
import site.siredvin.tweakium.modules.minecraft.setup.TweakiumBlocks
import site.siredvin.tweakium.modules.minecraft.xplat.TweakiumPlatform

object TweakiumDataProviders {
    fun add(generator: GeneratorSink) {
        generator.add(DataProvider.Factory { output -> TweakiumLanguage(output, "en_us", "Creative Filler") })
        generator.add(DataProvider.Factory { output -> TweakiumLanguage(output, "uk_ua", "Творчий наповнювач") })
    }

    private class TweakiumLanguage(output: PackOutput, locale: String, private val name: String) : ComputerLanguageProvider(output, TweakiumCore.MOD_ID, locale, TweakiumPlatform.holder) {
        override fun addTranslations() {
            add(TweakiumBlocks.CREATIVE_FILLER.get(), name)
        }
    }
}
