package site.siredvin.tweakium.modules.platform.api

import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.api.upgrades.UpgradeType
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack

fun interface TurtleUpgradeFactory<V : ITurtleUpgrade> {
    fun build(id: ResourceLocation, type: UpgradeType<V>, item: ItemStack): V
}
