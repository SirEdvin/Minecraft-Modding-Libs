package site.siredvin.tweakium.modules.data

import net.minecraft.client.data.models.MultiVariant
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator
import net.minecraft.client.data.models.model.ItemModelUtils
import net.minecraft.client.data.models.model.ModelTemplates
import net.minecraft.client.data.models.model.TextureMapping
import net.minecraft.client.data.models.model.TextureSlot
import net.minecraft.client.renderer.block.dispatch.Variant
import net.minecraft.client.resources.model.sprite.Material
import net.minecraft.data.DataProvider
import net.minecraft.data.PackOutput
import net.minecraft.resources.Identifier
import net.minecraft.util.random.WeightedList
import site.siredvin.broccolium.modules.data.api.GeneratorSink
import site.siredvin.tweakium.TweakiumCore
import site.siredvin.tweakium.modules.minecraft.setup.TweakiumBlocks
import site.siredvin.tweakium.modules.minecraft.xplat.TweakiumPlatform

object TweakiumDataProviders {
    fun add(generator: GeneratorSink) {
        generator.add(DataProvider.Factory(::CreativeFillerTextures))
        generator.models({ models ->
            val block = TweakiumBlocks.CREATIVE_FILLER.get()
            val textures = TextureMapping()
                .put(TextureSlot.SIDE, Material(Identifier.fromNamespaceAndPath(TweakiumCore.MOD_ID, "block/creative_filler_side")))
                .put(TextureSlot.TOP, Material(Identifier.fromNamespaceAndPath(TweakiumCore.MOD_ID, "block/creative_filler_top")))
                .put(TextureSlot.BOTTOM, Material(Identifier.fromNamespaceAndPath(TweakiumCore.MOD_ID, "block/creative_filler_bottom")))
            val model = ModelTemplates.CUBE_BOTTOM_TOP.create(block, textures, models.modelOutput)
            models.blockStateOutput.accept(MultiVariantGenerator.dispatch(block, MultiVariant(WeightedList.of(Variant(model)))))
            models.itemModelOutput.accept(block.asItem(), ItemModelUtils.plainModel(model))
        }, {})
        generator.add(DataProvider.Factory { output -> TweakiumLanguage(output, "en_us", "Creative Filler") })
        generator.add(DataProvider.Factory { output -> TweakiumLanguage(output, "uk_ua", "Творчий наповнювач") })
    }

    private class TweakiumLanguage(output: PackOutput, locale: String, private val name: String) : ComputerLanguageProvider(output, TweakiumCore.MOD_ID, locale, TweakiumPlatform.holder) {
        override fun addTranslations() {
            add(TweakiumBlocks.CREATIVE_FILLER.get(), name)
        }
    }
}
