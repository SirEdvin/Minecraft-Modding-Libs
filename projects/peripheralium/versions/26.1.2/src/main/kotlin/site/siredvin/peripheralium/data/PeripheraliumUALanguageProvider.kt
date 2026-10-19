package site.siredvin.peripheralium.data

import net.minecraft.data.PackOutput
import site.siredvin.broccolium.modules.data.lang.LanguageProvider
import site.siredvin.peripheralium.Blocks
import site.siredvin.peripheralium.Items
import site.siredvin.peripheralium.PeripheraliumCore
import site.siredvin.peripheralium.PeripheraliumPlatform

class PeripheraliumUALanguageProvider(
    output: PackOutput,
) : LanguageProvider(output, PeripheraliumCore.MOD_ID, "uk_ua", PeripheraliumPlatform.holder, *PeripheraliumText.entries.toTypedArray()) {
    override fun addTranslations() {
        add(Items.PERIPHERALIUM_DUST.get(), "Перифераліумний пил")
        add(Items.PERIPHERALIUM_UPGRADE_TEMPLATE.get(), "Ковальский щаблон з перифераліуму")
        add(Blocks.PERIPHERALIUM_BLOCK.get(), "Блок перифераліуму")
        add(PeripheraliumText.CREATIVE_TAB, "Перифераліум")
    }
}
