package site.siredvin.tweakium.modules.platform

import dan200.computercraft.api.pocket.IPocketUpgrade
import dan200.computercraft.api.upgrades.UpgradeType
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import site.siredvin.broccolium.modules.platform.api.RegistryEntry
import site.siredvin.tweakium.modules.platform.api.PocketUpgradeFactory

class PocketUpgradeTypeRegistryEntry<V : IPocketUpgrade>(private val factory: PocketUpgradeFactory<V>, private val innerEntry: RegistryEntry<UpgradeType<V>>) : RegistryEntry<UpgradeType<V>> {
    fun createUpgrade(stack: ItemStack): V = factory.build(id, get(), stack)

    override val id: ResourceLocation
        get() = innerEntry.id

    override fun get(): UpgradeType<V> = innerEntry.get()
}
