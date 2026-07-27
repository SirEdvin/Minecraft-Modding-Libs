package site.siredvin.broccolium.modules.lookup

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity

fun interface BlockBasedLookup<T> {
    fun extract(level: Level, pos: BlockPos, blockEntity: BlockEntity?, direction: Direction?): T?
}
