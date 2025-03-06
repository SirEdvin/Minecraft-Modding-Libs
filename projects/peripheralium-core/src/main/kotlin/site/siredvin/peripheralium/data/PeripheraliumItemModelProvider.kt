package site.siredvin.peripheralium.data

import net.minecraft.data.models.ItemModelGenerators
import net.minecraft.data.models.model.ModelTemplates
import site.siredvin.peripheralium.Items

object PeripheraliumItemModelProvider {
    fun addModels(generators: ItemModelGenerators) {
        val peripheraliumDust = Items.PERIPHERALIUM_DUST.get()

        generators.generateFlatItem(peripheraliumDust, ModelTemplates.FLAT_ITEM)
        generators.generateFlatItem(Items.PERIPHERALIUM_UPGRADE_TEMPLATE.get(), ModelTemplates.FLAT_ITEM)
    }
}
