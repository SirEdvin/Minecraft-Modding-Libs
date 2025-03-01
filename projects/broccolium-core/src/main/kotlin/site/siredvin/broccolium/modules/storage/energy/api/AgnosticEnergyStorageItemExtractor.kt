package site.siredvin.broccolium.modules.storage.energy.api

import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

fun interface AgnosticEnergyStorageItemExtractor {
    fun extract(level: Level, stack: ItemStack): AgnosticEnergyStorage?
}
