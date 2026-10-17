package site.siredvin.tweakium.modules.platform.api

import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.api.upgrades.UpgradeType
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemStackTemplate

fun interface TurtleUpgradeFactory<V : ITurtleUpgrade> {
    fun build(id: Identifier, type: UpgradeType<V>, item: ItemStackTemplate): V
}
