package site.siredvin.broccolium.modules.base.block

import com.mojang.serialization.MapCodec
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import site.siredvin.broccolium.modules.base.codec.BlockCodec
import site.siredvin.broccolium.modules.base.util.BlockUtil
import site.siredvin.broccolium.modules.platform.api.RegistryEntry

class GenericBlockEntityBlock<T : BlockEntity>(
    blockEntityTypeSup: RegistryEntry<BlockEntityType<T>>,
    isRotatable: Boolean,
    belongToTickingEntity: Boolean = false,
    properties: Properties = BlockUtil.defaultProperties(),
) : FacingBlockEntityBlock<T>(blockEntityTypeSup, isRotatable, belongToTickingEntity, properties) {
    private val innerCodec by lazy {
        BlockCodec.buildCodec(::GenericBlockEntityBlock)
    }

    override fun codec(): MapCodec<out BaseEntityBlock> = innerCodec
}
