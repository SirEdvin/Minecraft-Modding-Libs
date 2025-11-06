package site.siredvin.broccolium.modules.storage.fluid

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import site.siredvin.broccolium.modules.lookup.ChainedBaseLookup
import site.siredvin.broccolium.modules.storage.fluid.api.AgnosticFluidSink
import site.siredvin.broccolium.modules.storage.fluid.api.AgnosticFluidSinkProvider
import site.siredvin.broccolium.modules.storage.fluid.api.AgnosticFluidStorage

object AgnosticFluidSinkLookup : ChainedBaseLookup<AgnosticFluidSink, AgnosticFluidStorage>(AgnosticFluidStorageLookup) {
    override fun extractFromBlock(
        level: Level,
        pos: BlockPos,
        blockEntity: BlockEntity?,
        direction: Direction?,
    ): AgnosticFluidSink? {
        if (blockEntity is AgnosticFluidSinkProvider) {
            return blockEntity.fluidSink
        }
        return super.extractFromBlock(level, pos, blockEntity, direction)
    }
}
