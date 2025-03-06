package site.siredvin.broccolium.modules.base.codec.api

import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockBehaviour.Properties
import site.siredvin.broccolium.modules.base.block.FacingBlockEntityBlock
import site.siredvin.broccolium.modules.platform.api.RegistryEntry

fun interface BlockConstructor<B : FacingBlockEntityBlock<T>, Z : BlockEntityType<T>, T : BlockEntity> {
    fun build(
        blockEntityTypeSup: RegistryEntry<Z>,
        isRotatable: Boolean,
        belongToTickingEntity: Boolean,
        properties: Properties,
    ): B
}
