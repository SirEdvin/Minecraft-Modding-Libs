package site.siredvin.broccolium.modules.data.api

import net.minecraft.resources.ResourceLocation
import net.minecraft.stats.Stat
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import java.util.function.Supplier

interface ModInformationHolder {
    val items: List<Supplier<out Item>>
        get() = emptyList()
    val blocks: List<Supplier<out Block>>
        get() = emptyList()
    val customStats: List<Supplier<Stat<ResourceLocation>>>
        get() = emptyList()
}
