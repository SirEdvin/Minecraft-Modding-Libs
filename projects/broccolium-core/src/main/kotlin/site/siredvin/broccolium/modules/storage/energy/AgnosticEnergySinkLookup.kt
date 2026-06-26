package site.siredvin.broccolium.modules.storage.energy

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import site.siredvin.broccolium.modules.lookup.ChainedBaseLookup
import site.siredvin.broccolium.modules.storage.energy.api.AgnosticEnergySink
import site.siredvin.broccolium.modules.storage.energy.api.AgnosticEnergySinkProvider
import site.siredvin.broccolium.modules.storage.energy.api.AgnosticEnergyStorage

object AgnosticEnergySinkLookup : ChainedBaseLookup<AgnosticEnergySink, AgnosticEnergyStorage>(AgnosticEnergyStorageLookup) {
    override fun extractFromBlock(
        level: Level,
        pos: BlockPos,
        blockEntity: BlockEntity?,
        direction: Direction?,
    ): AgnosticEnergySink? {
        if (blockEntity is AgnosticEnergySinkProvider) {
            return blockEntity.energySink
        }
        return super.extractFromBlock(level, pos, blockEntity, direction)
    }
}
