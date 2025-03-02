package site.siredvin.broccolium.modules.data

import net.minecraft.data.PackOutput
import site.siredvin.broccolium.BroccoliumCore
import site.siredvin.broccolium.modules.data.api.ModInformationHolder
import site.siredvin.broccolium.modules.data.lang.LanguageProvider

class LibUALanguageProvider(
    output: PackOutput,
) : LanguageProvider(output, BroccoliumCore.MOD_ID, "uk_ua", object : ModInformationHolder {}, *BroccoliumText.entries.toTypedArray()) {
    override fun addTranslations() {
        add(BroccoliumText.PRESS_FOR_DESCRIPTION, "[§3Left shift§r] показити опис")
        add(BroccoliumText.EMPTY_ENERGY, "Порожня енергія (якого біса?)")
        add(BroccoliumText.TURTLE_FUEL_ENERGY, "Паливо для черепах")
        add(BroccoliumText.FORGE_ENERGY, "Forge-енергія")
        add(BroccoliumText.RF_ENERGY, "RF енергія")
    }
}
