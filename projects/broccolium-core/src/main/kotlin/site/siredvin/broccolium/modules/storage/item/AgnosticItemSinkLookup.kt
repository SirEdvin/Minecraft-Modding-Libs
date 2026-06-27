package site.siredvin.broccolium.modules.storage.item

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import site.siredvin.broccolium.modules.lookup.ChainedBaseLookup
import site.siredvin.broccolium.modules.storage.base.api.AgnosticSink
import site.siredvin.broccolium.modules.storage.base.api.AgnosticStorage
import site.siredvin.broccolium.modules.storage.item.api.AgnosticItemSinkProvider

object AgnosticItemSinkLookup : ChainedBaseLookup<AgnosticSink<ItemStack, Int>, AgnosticStorage<ItemStack, Int>>(AgnosticItemStorageLookup) {
    override fun extractFromBlock(
        level: Level,
        pos: BlockPos,
        blockEntity: BlockEntity?,
        direction: Direction?,
    ): AgnosticSink<ItemStack, Int>? {
        if (blockEntity is AgnosticItemSinkProvider) {
            return blockEntity.itemSink
        }
        return super.extractFromBlock(level, pos, blockEntity, direction)
    }
}
