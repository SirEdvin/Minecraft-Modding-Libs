package site.siredvin.broccolium.modules.storage.fluid.api

import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity

fun interface AgnosticFluidStorageExtractor {
    fun extract(level: Level, pos: BlockPos, blockEntity: BlockEntity?): AgnosticFluidStorage?
}
