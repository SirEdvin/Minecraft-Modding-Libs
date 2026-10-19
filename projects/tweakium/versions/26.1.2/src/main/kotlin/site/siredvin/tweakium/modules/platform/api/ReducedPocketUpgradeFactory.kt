package site.siredvin.tweakium.modules.platform.api

import dan200.computercraft.api.pocket.IPocketUpgrade
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemStackTemplate

fun interface ReducedPocketUpgradeFactory<V : IPocketUpgrade> {
    fun build(id: Identifier, item: ItemStackTemplate): V
}
