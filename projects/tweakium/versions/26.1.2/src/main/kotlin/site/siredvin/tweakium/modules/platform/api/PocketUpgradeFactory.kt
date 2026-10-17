package site.siredvin.tweakium.modules.platform.api

import dan200.computercraft.api.pocket.IPocketUpgrade
import dan200.computercraft.api.upgrades.UpgradeType
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemStackTemplate

fun interface PocketUpgradeFactory<V : IPocketUpgrade> {
    fun build(id: Identifier, type: UpgradeType<V>, item: ItemStackTemplate): V
}
