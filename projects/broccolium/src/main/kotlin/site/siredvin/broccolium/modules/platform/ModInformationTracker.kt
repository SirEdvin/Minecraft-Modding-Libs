package site.siredvin.broccolium.modules.platform

import net.minecraft.resources.ResourceLocation
import net.minecraft.stats.Stat
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import site.siredvin.broccolium.modules.data.api.ModInformationHolder
import java.util.function.Supplier

open class ModInformationTracker : ModInformationHolder {

    val internalItems: MutableList<Supplier<out Item>> = mutableListOf()
    val internalBlocks: MutableList<Supplier<out Block>> = mutableListOf()
    val internalCustomStats: MutableList<Supplier<Stat<ResourceLocation>>> = mutableListOf()

    override val items: List<Supplier<out Item>>
        get() = internalItems
    override val blocks: List<Supplier<out Block>>
        get() = internalBlocks
    override val customStats: List<Supplier<Stat<ResourceLocation>>>
        get() = internalCustomStats
}
