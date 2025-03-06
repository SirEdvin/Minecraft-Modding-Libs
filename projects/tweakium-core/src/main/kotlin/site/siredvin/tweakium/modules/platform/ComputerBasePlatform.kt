package site.siredvin.tweakium.modules.platform

import dan200.computercraft.api.pocket.IPocketUpgrade
import dan200.computercraft.api.turtle.ITurtleUpgrade
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
        upgrade: V,
    ): RegistryEntry<V> = registerTurtleUpgrade(ResourceLocation.fromNamespaceAndPath(baseInnerPlatform.modID, name), upgrade)

    fun <V : ITurtleUpgrade> registerTurtleUpgrade(
        key: ResourceLocation,
        upgrade: V,
    ): RegistryEntry<V> = SimpleRegistryEntry(key, baseInnerPlatform.registerTurtleUpgrade(key, upgrade))

    fun <V : IPocketUpgrade> registerPocketUpgrade(
        name: String,
        upgrade: V,
    ): RegistryEntry<V> = registerPocketUpgrade(ResourceLocation.fromNamespaceAndPath(baseInnerPlatform.modID, name), upgrade)

    fun <V : IPocketUpgrade> registerPocketUpgrade(
        key: ResourceLocation,
        upgrade: V,
    ): RegistryEntry<V> = SimpleRegistryEntry(key, baseInnerPlatform.registerPocketUpgrade(key, upgrade))
}
