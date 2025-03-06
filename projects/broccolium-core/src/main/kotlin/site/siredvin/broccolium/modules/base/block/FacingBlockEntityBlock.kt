package site.siredvin.broccolium.modules.base.block

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.DirectionProperty
import site.siredvin.broccolium.modules.base.util.BlockUtil
import site.siredvin.broccolium.modules.platform.api.RegistryEntry

abstract class FacingBlockEntityBlock<T : BlockEntity>(
    val blockEntityTypeSup: RegistryEntry<BlockEntityType<T>>,
    val isRotatable: Boolean,
    belongToTickingEntity: Boolean = false,
    properties: Properties = BlockUtil.defaultProperties(),
) : BaseBlockEntityBlock<T>(belongToTickingEntity, properties) {

    companion object {
        val FACING: DirectionProperty = HorizontalDirectionalBlock.FACING
    }

    init {
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.SOUTH))
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? = blockEntityTypeSup.get().create(pos, state)

    @Deprecated("Deprecated in Java")
    override fun rotate(state: BlockState, rot: Rotation): BlockState = if (isRotatable) {
        state.setValue(
            FACING,
            rot.rotate(state.getValue(FACING)),
        )
    } else {
        state
    }

    @Deprecated("Deprecated in Java")
    override fun mirror(state: BlockState, mirrorIn: Mirror): BlockState {
        @Suppress("KotlinRedundantDiagnosticSuppress", "DEPRECATION")
        return if (isRotatable) state.rotate(mirrorIn.getRotation(state.getValue(FACING))) else state
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING)
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? = if (isRotatable) {
        defaultBlockState().setValue(
            FACING,
            context.horizontalDirection.opposite,
        )
    } else {
        defaultBlockState()
    }
}
