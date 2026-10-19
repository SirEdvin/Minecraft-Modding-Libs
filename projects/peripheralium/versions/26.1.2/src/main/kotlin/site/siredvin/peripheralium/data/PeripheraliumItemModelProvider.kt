package site.siredvin.peripheralium.data

import net.minecraft.client.data.models.ItemModelGenerators
import net.minecraft.resources.Identifier
import site.siredvin.broccolium.modules.data.model.createFlatItem
import site.siredvin.peripheralium.Items

object PeripheraliumItemModelProvider {
    fun addModels(generators: ItemModelGenerators) {
        createFlatItem(generators, Items.PERIPHERALIUM_DUST.get(), Identifier.parse("peripheralium:item/peripheralium_dust"))
        createFlatItem(generators, Items.PERIPHERALIUM_UPGRADE_TEMPLATE.get(), Identifier.parse("peripheralium:item/peripheralium_upgrade_template"))
    }
}
