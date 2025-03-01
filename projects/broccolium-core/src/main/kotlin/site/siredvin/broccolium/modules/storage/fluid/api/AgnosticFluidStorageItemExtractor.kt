package site.siredvin.broccolium.modules.storage.fluid.api

import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

fun interface AgnosticFluidStorageItemExtractor {
    fun extract(level: Level, stack: ItemStack): AgnosticFluidStorage?
}
