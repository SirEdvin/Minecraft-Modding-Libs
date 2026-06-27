package site.siredvin.broccolium.modules.storage.fluid

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import site.siredvin.broccolium.modules.lookup.BaseLookup
import site.siredvin.broccolium.modules.storage.fluid.api.*

object AgnosticFluidStorageLookup : BaseLookup<AgnosticFluidStorage>() {

    override fun extractFromBlock(
        level: Level,
        pos: BlockPos,
        blockEntity: BlockEntity?,
        direction: Direction?,
    ): AgnosticFluidStorage? {
        if (blockEntity is AgnosticFluidStorageProvider) {
            return blockEntity.fluidStorage
        }
        return super.extractFromBlock(level, pos, blockEntity, direction)
    }
}
