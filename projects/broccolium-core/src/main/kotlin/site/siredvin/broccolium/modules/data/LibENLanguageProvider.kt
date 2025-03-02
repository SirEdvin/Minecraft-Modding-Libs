package site.siredvin.broccolium.modules.data

import net.minecraft.data.PackOutput
import site.siredvin.broccolium.BroccoliumCore
import site.siredvin.broccolium.modules.data.BroccoliumText
import site.siredvin.broccolium.modules.data.api.ModInformationHolder
import site.siredvin.broccolium.modules.data.lang.LanguageProvider

class LibENLanguageProvider(
    output: PackOutput,
) : LanguageProvider(output, BroccoliumCore.MOD_ID, "en_us", object : ModInformationHolder {}, *BroccoliumText.entries.toTypedArray()) {
    override fun addTranslations() {
        add(BroccoliumText.PRESS_FOR_DESCRIPTION, "[§3Left shift§r] show description")
        add(BroccoliumText.EMPTY_ENERGY, "Empty energy (like, what?)")
        add(BroccoliumText.TURTLE_FUEL_ENERGY, "Turtle fuel")
        add(BroccoliumText.FORGE_ENERGY, "Forge energy")
    }
}
