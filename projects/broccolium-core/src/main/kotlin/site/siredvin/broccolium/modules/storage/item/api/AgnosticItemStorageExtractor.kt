package site.siredvin.broccolium.modules.storage.item.api

import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity

fun interface AgnosticItemStorageExtractor {
    fun extract(level: Level, pos: BlockPos, blockEntity: BlockEntity?): AgnosticItemStorage?
}
