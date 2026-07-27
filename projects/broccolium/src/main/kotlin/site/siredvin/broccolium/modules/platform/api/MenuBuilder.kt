package site.siredvin.broccolium.modules.platform.api

import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.AbstractContainerMenu

fun interface MenuBuilder<M : AbstractContainerMenu> {
    fun build(id: Int, player: Inventory, data: FriendlyByteBuf): M
}
