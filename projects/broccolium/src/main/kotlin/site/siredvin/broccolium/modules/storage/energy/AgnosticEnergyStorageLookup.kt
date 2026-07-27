package site.siredvin.broccolium.modules.storage.energy

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import site.siredvin.broccolium.modules.lookup.BaseLookup
import site.siredvin.broccolium.modules.storage.energy.api.*

object AgnosticEnergyStorageLookup : BaseLookup<AgnosticEnergyStorage>() {
    override fun extractFromBlock(
        level: Level,
        pos: BlockPos,
        blockEntity: BlockEntity?,
        direction: Direction?,
    ): AgnosticEnergyStorage? {
        if (blockEntity is AgnosticEnergyStorageProvider) {
            return blockEntity.energyStorage
        }
        return super.extractFromBlock(level, pos, blockEntity, direction)
    }
}
