package site.siredvin.broccolium.modules.base.block

import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.state.BlockState
import site.siredvin.broccolium.modules.base.util.BlockUtil

class BaseBlock @JvmOverloads constructor(
    properties: Properties = BlockUtil.defaultProperties(),
) : Block(properties) {
    @Deprecated("Deprecated in Java")
    override fun getRenderShape(state: BlockState): RenderShape = RenderShape.MODEL
}
