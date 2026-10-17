package site.siredvin.broccolium.modules.base.api

import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.redstone.Orientation

interface IObservingBlockEntity {
    fun placed() {}
    fun destroy() {}

    /** Native 26.1 notifications do not identify a causal neighbor position. */
    fun onNeighbourChange(neighbourBlock: Block, orientation: Orientation?) {}
    fun blockTick() {}

    fun setPlacedBy(entity: LivingEntity?, stack: ItemStack) {}
}
