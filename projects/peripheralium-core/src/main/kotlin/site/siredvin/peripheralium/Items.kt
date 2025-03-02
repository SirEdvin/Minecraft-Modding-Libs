package site.siredvin.peripheralium

import net.minecraft.world.item.Item
import site.siredvin.broccolium.modules.base.item.DescriptiveItem

object Items {
    val PERIPHERALIUM_BLEND = PeripheraliumPlatform.registerItem("peripheralium_blend") { DescriptiveItem(Item.Properties()) }
    val PERIPHERALIUM_DUST = PeripheraliumPlatform.registerItem("peripheralium_dust") { DescriptiveItem(Item.Properties()) }
    val PERIPHERALIUM_UPGRADE_TEMPLATE = PeripheraliumPlatform.registerItem(
        "peripheralium_upgrade_template",
    ) { DescriptiveItem(Item.Properties()) }

    fun doSomething() {
    }
}
