package site.siredvin.periparium

import net.minecraft.data.PackOutput
import site.siredvin.broccolium.modules.data.BroccoliumText
import site.siredvin.peripheralium.PeripheraliumCore
import site.siredvin.peripheralium.common.setup.Blocks
import site.siredvin.peripheralium.common.setup.Items
import site.siredvin.peripheralium.data.language.LanguageProvider
import site.siredvin.peripheralium.xplat.LibPlatform

class LibENLanguageProvider(
    output: PackOutput,
) : LanguageProvider(output, PeripheraliumCore.MOD_ID, "en_us", LibPlatform.holder, *BroccoliumText.values()) {
    override fun addTranslations() {
        add(Items.PERIPHERALIUM_DUST.get(), "Peripheralium dust")
        add(Items.PERIPHERALIUM_BLEND.get(), "Peripheralium blend")
        add(Blocks.PERIPHERALIUM_BLOCK.get(), "Peripheralium block")
        add(Items.PERIPHERALIUM_UPGRADE_TEMPLATE.get(), "Peripheralium upgrade template")
        add(BroccoliumText.CREATIVE_TAB, "Peripheralium")
        add(BroccoliumText.PRESS_FOR_DESCRIPTION, "[§3Left shift§r] show description")
        add(BroccoliumText.EMPTY_ENERGY, "Empty energy (like, what?)")
        add(BroccoliumText.TURTLE_FUEL_ENERGY, "Turtle fuel")
        add(BroccoliumText.FORGE_ENERGY, "Forge energy")
    }
}
