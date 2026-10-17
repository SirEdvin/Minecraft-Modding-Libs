package site.siredvin.broccolium.modules.base.block

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.redstone.Orientation
import site.siredvin.broccolium.modules.base.api.IObservingBlockEntity
import site.siredvin.broccolium.modules.base.api.IOwnedBlockEntity
import site.siredvin.broccolium.modules.base.api.ITickingBlockEntity
import site.siredvin.broccolium.modules.base.util.BlockUtil

abstract class BaseBlockEntityBlock<T : BlockEntity>(
    val belongToTickingEntity: Boolean,
    properties: Properties = BlockUtil.defaultProperties(),
) : BaseEntityBlock(properties) {

    override fun getRenderShape(blockState: BlockState): RenderShape = RenderShape.MODEL

    override fun <T : BlockEntity> getTicker(
        tickerLevel: Level,
        tickerState: BlockState,
        type: BlockEntityType<T>,
    ): BlockEntityTicker<T>? {
        if (tickerLevel.isClientSide || !belongToTickingEntity) {
            return null
        }
        return BlockEntityTicker { level, pos, state, entity ->
            if (entity is ITickingBlockEntity) {
                entity.handleTick(level, pos, state)
            }
        }
    }

    override fun neighborChanged(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        neighbourBlock: Block,
        orientation: Orientation?,
        movedByPiston: Boolean,
    ) {
        super.neighborChanged(blockState, level, blockPos, neighbourBlock, orientation, movedByPiston)
        val tile = level.getBlockEntity(blockPos)
        if (tile is IObservingBlockEntity) {
            tile.onNeighbourChange(neighbourBlock, orientation)
        }
    }

    override fun tick(blockState: BlockState, serverLevel: ServerLevel, blockPos: BlockPos, random: RandomSource) {
        super.tick(blockState, serverLevel, blockPos, random)
        val tile = serverLevel.getBlockEntity(blockPos)
        if (tile is IObservingBlockEntity) {
            tile.blockTick()
        }
    }

    override fun onPlace(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        oldState: BlockState,
        movedByPiston: Boolean,
    ) {
        super.onPlace(blockState, level, blockPos, oldState, movedByPiston)
        if (blockState.block === this && oldState.block !== this) {
            val tile = level.getBlockEntity(blockPos)
            if (tile is IObservingBlockEntity) {
                tile.placed()
            }
        }
    }

    override fun setPlacedBy(level: Level, pos: BlockPos, state: BlockState, entity: LivingEntity?, stack: ItemStack) {
        super.setPlacedBy(level, pos, state, entity, stack)
        val tile = level.getBlockEntity(pos)
        if (tile is IOwnedBlockEntity && !level.isClientSide && entity is Player) {
            tile.player = entity
        }
        if (tile is IObservingBlockEntity) {
            tile.setPlacedBy(entity, stack)
        }
    }

    // Native removal now belongs to LevelChunk, not Block.onRemove. The target
    // LevelChunkMixin preserves destroy() after native block-entity removal.
}
