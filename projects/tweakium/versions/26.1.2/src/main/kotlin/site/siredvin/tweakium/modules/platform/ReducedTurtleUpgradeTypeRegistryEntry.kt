package site.siredvin.tweakium.modules.platform

import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.api.upgrades.UpgradeType
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemStackTemplate
import site.siredvin.broccolium.modules.platform.api.RegistryEntry
import site.siredvin.tweakium.modules.platform.api.ReducedTurtleUpgradeFactory

class ReducedTurtleUpgradeTypeRegistryEntry<V : ITurtleUpgrade>(private val factory: ReducedTurtleUpgradeFactory<V>, private val innerEntry: RegistryEntry<UpgradeType<V>>) : RegistryEntry<UpgradeType<V>> {
    fun createUpgrade(stack: ItemStackTemplate): V = factory.build(id, stack)

    override val id: Identifier
        get() = innerEntry.id

    override fun get(): UpgradeType<V> = innerEntry.get()
}
