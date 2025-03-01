package site.siredvin.broccolium.modules.storage.item.api

import net.minecraft.world.item.ItemStack

interface AccessibleAgnosticItemStorage : AgnosticItemStorage {
    fun getItem(slot: Int): ItemStack
}
