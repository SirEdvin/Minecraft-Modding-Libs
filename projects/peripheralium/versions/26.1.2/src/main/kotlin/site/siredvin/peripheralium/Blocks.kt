package site.siredvin.peripheralium

import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.block.Block
import site.siredvin.broccolium.modules.base.util.BlockUtil

object Blocks {
    val PERIPHERALIUM_BLOCK = PeripheraliumPlatform.registerBlock("peripheralium_block", {
        Block(
            BlockUtil.defaultProperties(destroyTime = 0.5f).setId(
                ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(PeripheraliumCore.MOD_ID, "peripheralium_block")),
            ),
        )
    })

    fun doSomething() {
    }
}
