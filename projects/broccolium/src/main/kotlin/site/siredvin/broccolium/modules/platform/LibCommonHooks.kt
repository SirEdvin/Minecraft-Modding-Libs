package site.siredvin.broccolium.modules.platform

import net.minecraft.world.entity.Entity
import net.minecraft.world.item.ItemStack
import site.siredvin.broccolium.modules.tricks.DropConsumer

object LibCommonHooks {
    fun onEntitySpawn(entity: Entity): Boolean = DropConsumer.onEntitySpawn(entity)

    fun onLivingDrop(entity: Entity, stack: ItemStack?): Boolean = DropConsumer.onLivingDrop(entity, stack!!)
}
