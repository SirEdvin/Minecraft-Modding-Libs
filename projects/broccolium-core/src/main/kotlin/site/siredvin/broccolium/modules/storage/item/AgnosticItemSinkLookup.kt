package site.siredvin.broccolium.modules.storage.item

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import site.siredvin.broccolium.modules.lookup.ChainedBaseLookup
import site.siredvin.broccolium.modules.storage.item.api.AgnosticItemSink
import site.siredvin.broccolium.modules.storage.item.api.AgnosticItemSinkProvider
import site.siredvin.broccolium.modules.storage.item.api.AgnosticItemStorage

object AgnosticItemSinkLookup : ChainedBaseLookup<AgnosticItemSink, AgnosticItemStorage>(AgnosticItemStorageLookup) {
    override fun extractFromBlock(
        level: Level,
        pos: BlockPos,
        blockEntity: BlockEntity?,
        direction: Direction?,
    ): AgnosticItemSink? {
        if (blockEntity is AgnosticItemSinkProvider) {
            return blockEntity.itemSink
        }
        return super.extractFromBlock(level, pos, blockEntity, direction)
    }
}
