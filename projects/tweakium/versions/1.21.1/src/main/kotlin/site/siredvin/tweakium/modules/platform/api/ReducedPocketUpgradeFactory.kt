package site.siredvin.tweakium.modules.platform.api

import dan200.computercraft.api.pocket.IPocketUpgrade
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack

fun interface ReducedPocketUpgradeFactory<V : IPocketUpgrade> {
    fun build(id: ResourceLocation, item: ItemStack): V
}
