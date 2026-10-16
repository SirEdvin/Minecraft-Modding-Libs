package site.siredvin.broccolium.testmod

import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.CustomData

internal fun bridgeStack(count: Int, marker: String): ItemStack = ItemStack(Items.DIAMOND, count).apply {
    set(DataComponents.CUSTOM_DATA, CustomData.of(CompoundTag().apply { putString("bridge_marker", marker) }))
}
