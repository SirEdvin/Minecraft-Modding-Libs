package site.siredvin.broccolium.modules.base.api

import net.minecraft.core.BlockPos
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack

interface IObservingBlockEntity {
    fun placed() {}
    fun destroy() {}
    fun onNeighbourChange(neighbour: BlockPos) {}
    fun blockTick() {}

    fun setPlacedBy(entity: LivingEntity?, stack: ItemStack) {}
}
