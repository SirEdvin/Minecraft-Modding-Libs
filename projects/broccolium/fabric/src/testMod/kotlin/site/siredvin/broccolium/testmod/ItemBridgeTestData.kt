package site.siredvin.broccolium.testmod

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

internal fun bridgeStack(count: Int, marker: String): ItemStack = ItemStack(Items.DIAMOND, count).apply {
    tag = CompoundTag().apply { putString("bridge_marker", marker) }
}
