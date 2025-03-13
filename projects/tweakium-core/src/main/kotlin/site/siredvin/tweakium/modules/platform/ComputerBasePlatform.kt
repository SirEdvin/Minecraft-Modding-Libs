package site.siredvin.tweakium.modules.platform

import dan200.computercraft.api.pocket.IPocketUpgrade
import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.api.upgrades.UpgradeType
import net.minecraft.resources.ResourceLocation
import site.siredvin.broccolium.modules.platform.BasePlatform
import site.siredvin.broccolium.modules.platform.SimpleRegistryEntry
import site.siredvin.broccolium.modules.platform.api.RegistryEntry
import site.siredvin.tweakium.modules.data.ComputerModInformationHolder
import site.siredvin.tweakium.modules.platform.api.InnerComputerBasePlatform

abstract class ComputerBasePlatform : BasePlatform() {
    abstract override val baseInnerPlatform: InnerComputerBasePlatform
    abstract override val modInformationTracker: ComputerModInformationTracker

    @Suppress("UNCHECKED_CAST")
    override val holder: ComputerModInformationHolder
        get() = modInformationTracker

    fun <V : ITurtleUpgrade> registerTurtleUpgrade(
        name: String,
        upgrade: UpgradeType<V>,
    ): RegistryEntry<UpgradeType<V>> = registerTurtleUpgrade(ResourceLocation.fromNamespaceAndPath(baseInnerPlatform.modID, name), upgrade)

    fun <V : ITurtleUpgrade> registerTurtleUpgrade(
        key: ResourceLocation,
        upgrade: UpgradeType<V>,
    ): RegistryEntry<UpgradeType<V>> {
        val result = SimpleRegistryEntry(key, baseInnerPlatform.registerTurtleUpgrade(key, upgrade))
        @Suppress("UNCHECKED_CAST")
        modInformationTracker.internalTurtleUpgrades.add(result as RegistryEntry<UpgradeType<out ITurtleUpgrade>>)
        return result
    }

    fun <V : IPocketUpgrade> registerPocketUpgrade(
        name: String,
        upgrade: UpgradeType<V>,
    ): RegistryEntry<UpgradeType<V>> = registerPocketUpgrade(ResourceLocation.fromNamespaceAndPath(baseInnerPlatform.modID, name), upgrade)

    fun <V : IPocketUpgrade> registerPocketUpgrade(
        key: ResourceLocation,
        upgrade: UpgradeType<V>,
    ): RegistryEntry<UpgradeType<V>> {
        val result = SimpleRegistryEntry(key, baseInnerPlatform.registerPocketUpgrade(key, upgrade))
        @Suppress("UNCHECKED_CAST")
        modInformationTracker.internalPocketUpgrades.add(result as RegistryEntry<UpgradeType<out IPocketUpgrade>>)
        return result
    }
}
