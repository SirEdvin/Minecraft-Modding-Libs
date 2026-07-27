package site.siredvin.peripheralium.data

import net.minecraft.data.PackOutput
import site.siredvin.broccolium.modules.data.lang.LanguageProvider
import site.siredvin.peripheralium.Blocks
import site.siredvin.peripheralium.Items
import site.siredvin.peripheralium.PeripheraliumCore
import site.siredvin.peripheralium.PeripheraliumPlatform

class PeripheraliumENLanguageProvider(
    output: PackOutput,
) : LanguageProvider(output, PeripheraliumCore.MOD_ID, "en_us", PeripheraliumPlatform.holder, *PeripheraliumText.entries.toTypedArray()) {
    override fun addTranslations() {
        add(Items.PERIPHERALIUM_DUST.get(), "Peripheralium dust")
        add(Items.PERIPHERALIUM_BLEND.get(), "Peripheralium blend", "Sad and obsolete now, here just for compatibility reasons")
        add(Blocks.PERIPHERALIUM_BLOCK.get(), "Peripheralium block")
        add(Items.PERIPHERALIUM_UPGRADE_TEMPLATE.get(), "Peripheralium upgrade template")
        add(PeripheraliumText.CREATIVE_TAB, "Peripheralium")
    }
}
