package site.siredvin.peripheralium.data

import net.minecraft.client.data.models.BlockModelGenerators
import site.siredvin.broccolium.modules.data.model.genericBlock
import site.siredvin.peripheralium.Blocks

object PeripheraliumBlockModelProvider {
    fun addModels(generators: BlockModelGenerators) {
        genericBlock(generators, Blocks.PERIPHERALIUM_BLOCK.get())
    }
}
