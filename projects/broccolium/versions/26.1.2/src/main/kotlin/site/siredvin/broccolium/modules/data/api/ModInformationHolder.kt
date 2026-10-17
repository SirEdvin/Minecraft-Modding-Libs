package site.siredvin.broccolium.modules.data.api

import net.minecraft.resources.Identifier
import net.minecraft.stats.Stat
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import site.siredvin.broccolium.modules.platform.api.RegistryEntry

interface ModInformationHolder {
    val items: List<RegistryEntry<out Item>>
        get() = emptyList()
    val blocks: List<RegistryEntry<out Block>>
        get() = emptyList()
    val customStats: List<RegistryEntry<Stat<Identifier>>>
        get() = emptyList()
}
