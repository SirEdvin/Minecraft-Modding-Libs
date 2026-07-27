package site.siredvin.peripheralium

import net.minecraft.world.level.block.Block
import site.siredvin.broccolium.modules.base.util.BlockUtil

object Blocks {
    val PERIPHERALIUM_BLOCK = PeripheraliumPlatform.registerBlock("peripheralium_block", { Block(BlockUtil.defaultProperties(destroyTime = 0.5f)) })

    fun doSomething() {
    }
}
