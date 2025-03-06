package site.siredvin.broccolium.modules.platform

import net.minecraft.resources.ResourceLocation
import net.minecraft.stats.Stat
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import site.siredvin.broccolium.modules.data.api.ModInformationHolder
import site.siredvin.broccolium.modules.platform.api.RegistryEntry

open class ModInformationTracker : ModInformationHolder {

    val internalItems: MutableList<RegistryEntry<out Item>> = mutableListOf()
    val internalBlocks: MutableList<RegistryEntry<out Block>> = mutableListOf()
    val internalCustomStats: MutableList<RegistryEntry<Stat<ResourceLocation>>> = mutableListOf()

    override val items: List<RegistryEntry<out Item>>
        get() = internalItems
    override val blocks: List<RegistryEntry<out Block>>
        get() = internalBlocks
    override val customStats: List<RegistryEntry<Stat<ResourceLocation>>>
        get() = internalCustomStats
}
