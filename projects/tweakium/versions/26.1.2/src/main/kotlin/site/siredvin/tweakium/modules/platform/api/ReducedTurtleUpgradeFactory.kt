package site.siredvin.tweakium.modules.platform.api

import dan200.computercraft.api.turtle.ITurtleUpgrade
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemStackTemplate

fun interface ReducedTurtleUpgradeFactory<V : ITurtleUpgrade> {
    fun build(id: Identifier, item: ItemStackTemplate): V
}
