package site.siredvin.tweakium.modules.platform.api

import dan200.computercraft.api.turtle.ITurtleUpgrade
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack

fun interface ReducedTurtleUpgradeFactory<V : ITurtleUpgrade> {
    fun build(id: ResourceLocation, item: ItemStack): V
}
