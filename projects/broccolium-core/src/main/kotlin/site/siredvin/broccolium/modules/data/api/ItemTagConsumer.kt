package site.siredvin.broccolium.modules.data.api

import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block

interface ItemTagConsumer : TagConsumer<Item> {
    // TODO: copy function doesn't work quite well for fabric, so I should address or remove it
    fun copy(block: TagKey<Block>, item: TagKey<Item>)
}
