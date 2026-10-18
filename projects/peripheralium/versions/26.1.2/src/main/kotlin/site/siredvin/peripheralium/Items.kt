package site.siredvin.peripheralium

import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.Item
import site.siredvin.broccolium.modules.base.item.DescriptiveItem

object Items {
    private fun properties(name: String): Item.Properties = Item.Properties().setId(
        ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(PeripheraliumCore.MOD_ID, name)),
    )

    val PERIPHERALIUM_DUST = PeripheraliumPlatform.registerItem("peripheralium_dust") {
        DescriptiveItem(properties("peripheralium_dust"))
    }
    val PERIPHERALIUM_UPGRADE_TEMPLATE = PeripheraliumPlatform.registerItem("peripheralium_upgrade_template") {
        DescriptiveItem(properties("peripheralium_upgrade_template"))
    }

    fun doSomething() {
    }
}
