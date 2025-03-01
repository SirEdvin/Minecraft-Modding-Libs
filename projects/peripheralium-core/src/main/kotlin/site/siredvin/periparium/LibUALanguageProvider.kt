package site.siredvin.periparium

import net.minecraft.data.PackOutput
import site.siredvin.broccolium.modules.data.BroccoliumText
import site.siredvin.peripheralium.PeripheraliumCore
import site.siredvin.peripheralium.common.setup.Blocks
import site.siredvin.peripheralium.common.setup.Items
import site.siredvin.peripheralium.data.language.LanguageProvider
import site.siredvin.peripheralium.xplat.LibPlatform

class LibUALanguageProvider(
    output: PackOutput,
) : LanguageProvider(output, PeripheraliumCore.MOD_ID, "uk_ua", LibPlatform.holder, *BroccoliumText.values()) {
    override fun addTranslations() {
        add(Items.PERIPHERALIUM_DUST.get(), "Перифераліумний пил")
        add(Items.PERIPHERALIUM_BLEND.get(), "Сирий перифераліум")
        add(Items.PERIPHERALIUM_UPGRADE_TEMPLATE.get(), "Ковальский щаблон з перифераліуму")
        add(Blocks.PERIPHERALIUM_BLOCK.get(), "Блок перифераліуму")
        add(BroccoliumText.CREATIVE_TAB, "Перифераліум")
        add(BroccoliumText.PRESS_FOR_DESCRIPTION, "[§3Left shift§r] показити опис")
        add(BroccoliumText.EMPTY_ENERGY, "Порожня енергія (якого біса?)")
        add(BroccoliumText.TURTLE_FUEL_ENERGY, "Паливо для черепах")
        add(BroccoliumText.FORGE_ENERGY, "Forge-енергія")
    }
}
