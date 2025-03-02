package site.siredvin.tweakium.modules.data

import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import site.siredvin.broccolium.modules.data.api.TextRecord
import site.siredvin.broccolium.modules.data.lang.LanguageProvider
import site.siredvin.tweakium.modules.platform.ComputerPlatformRegistries
import java.util.stream.Stream

abstract class ComputerLanguageProvider(
    output: PackOutput,
    modID: String,
    locale: String,
    override val informationHolder: ComputerModInformationHolder,
    vararg textRecords: TextRecord,
) : LanguageProvider(output, modID, locale, informationHolder, *textRecords) {

    override fun getExpectedKeys(): Stream<String> = Stream.concat(
        super.getExpectedKeys(),
        Stream.of(
            informationHolder.turtleSerializers.stream().map { ComputerPlatformRegistries.TURTLE_SERIALIZERS.getKey(it.get()).toTurtleTranslationKey() },
            informationHolder.pocketSerializers.stream().map { ComputerPlatformRegistries.POCKET_SERIALIZERS.getKey(it.get()).toPocketTranslationKey() },
        ).flatMap { it },
    )

    fun addPocket(id: ResourceLocation, text: String) {
        add(id.toPocketTranslationKey(), text)
    }

    fun addTurtle(id: ResourceLocation, text: String) {
        add(id.toTurtleTranslationKey(), text)
    }

    fun addUpgrades(id: ResourceLocation, text: String) {
        addPocket(id, text)
        addTurtle(id, text)
    }

    override fun getName(): String = "ComputerLanguage$locale"
}
