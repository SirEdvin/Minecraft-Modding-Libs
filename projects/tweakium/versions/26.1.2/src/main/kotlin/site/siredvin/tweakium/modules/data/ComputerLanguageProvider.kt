package site.siredvin.tweakium.modules.data

import net.minecraft.data.PackOutput
import net.minecraft.resources.Identifier
import site.siredvin.broccolium.modules.data.api.TextRecord
import site.siredvin.broccolium.modules.data.lang.LanguageProvider
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
            informationHolder.turtleUpgrades.stream().map { it.id.toTurtleTranslationKey() },
            informationHolder.pocketUpgrades.stream().map { it.id.toPocketTranslationKey() },
        ).flatMap { it },
    )

    fun addPocket(id: Identifier, text: String) {
        add(id.toPocketTranslationKey(), text)
    }

    fun addTurtle(id: Identifier, text: String) {
        add(id.toTurtleTranslationKey(), text)
    }

    fun addUpgrades(id: Identifier, text: String) {
        addPocket(id, text)
        addTurtle(id, text)
    }

    override fun getName(): String = "ComputerLanguage$locale"
}
