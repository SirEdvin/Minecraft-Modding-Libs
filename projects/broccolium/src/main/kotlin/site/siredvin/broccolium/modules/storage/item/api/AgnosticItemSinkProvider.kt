package site.siredvin.broccolium.modules.storage.item.api

import net.minecraft.world.item.ItemStack
import site.siredvin.broccolium.modules.storage.base.api.AgnosticSink

interface AgnosticItemSinkProvider {
    val itemSink: AgnosticSink<ItemStack, Int>
}
