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
import site.siredvin.broccolium.modules.base.api.IObservingBlockEntity
import site.siredvin.broccolium.modules.base.api.IOwnedBlockEntity
import site.siredvin.broccolium.modules.base.api.ITickingBlockEntity
import site.siredvin.broccolium.modules.base.util.BlockUtil

abstract class BaseBlockEntityBlock<T : BlockEntity>(
    private val belongToTickingEntity: Boolean,
    properties: Properties = BlockUtil.defaultProperties(),
) : BaseEntityBlock(properties) {

    @Deprecated("Deprecated in Java")
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

    @Deprecated("Deprecated in Java")
    override fun neighborChanged(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        neighbourBlock: Block,
        neighbourPos: BlockPos,
        bl: Boolean,
    ) {
        @Suppress("DEPRECATION")
        super.neighborChanged(blockState, level, blockPos, neighbourBlock, neighbourPos, bl)
        val tile = level.getBlockEntity(blockPos)
        if (tile is IObservingBlockEntity) {
            tile.onNeighbourChange(neighbourPos)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun tick(blockState: BlockState, serverLevel: ServerLevel, blockPos: BlockPos, random: RandomSource) {
        @Suppress("DEPRECATION")
        super.tick(blockState, serverLevel, blockPos, random)
        val tile = serverLevel.getBlockEntity(blockPos)
        if (tile is IObservingBlockEntity) {
            tile.blockTick()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onPlace(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        oldState: BlockState,
        bl: Boolean,
    ) {
        @Suppress("DEPRECATION")
        super.onPlace(blockState, level, blockPos, oldState, bl)
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

    @Deprecated("Deprecated in Java")
    override fun onRemove(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        replace: BlockState,
        bl: Boolean,
    ) {
        if (blockState.block === replace.block) {
            return
        }
        val tile = level.getBlockEntity(blockPos)
        @Suppress("DEPRECATION")
        super.onRemove(blockState, level, blockPos, replace, bl)
        if (tile is IObservingBlockEntity) {
            tile.destroy()
        }
    }
}
