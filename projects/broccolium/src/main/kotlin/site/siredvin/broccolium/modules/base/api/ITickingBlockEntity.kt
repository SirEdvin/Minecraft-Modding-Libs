package site.siredvin.broccolium.modules.base.api

import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState

interface ITickingBlockEntity {
    fun handleTick(level: Level, pos: BlockPos, state: BlockState) {}
}
