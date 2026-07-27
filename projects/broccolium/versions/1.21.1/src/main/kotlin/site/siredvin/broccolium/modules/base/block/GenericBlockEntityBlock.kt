package site.siredvin.broccolium.modules.base.block

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import site.siredvin.broccolium.modules.base.codec.BlockCodec
import site.siredvin.broccolium.modules.base.util.BlockUtil
import site.siredvin.broccolium.modules.platform.api.RegistryEntry
import java.util.function.Supplier

class GenericBlockEntityBlock<T : BlockEntity>(
    val blockEntityTypeSup: Supplier<RegistryEntry<BlockEntityType<T>>>,
    isRotatable: Boolean,
    belongToTickingEntity: Boolean = false,
    properties: Properties = BlockUtil.defaultProperties(),
) : FacingBlockEntityBlock<T>(isRotatable, belongToTickingEntity, properties) {
    private val innerCodec by lazy {
        BlockCodec.buildCodec<GenericBlockEntityBlock<T>, BlockEntityType<T>, T> { blockEntityTypeSup, isRotatable, belongToTickingEntity, properties ->
            GenericBlockEntityBlock(
                { blockEntityTypeSup },
                isRotatable,
                belongToTickingEntity,
                properties,
            )
        }
    }

    override fun codec(): MapCodec<out BaseEntityBlock> = innerCodec
    override fun newBlockEntity(p0: BlockPos, p1: BlockState): BlockEntity? = blockEntityTypeSup.get().get().create(p0, p1)
}
