package site.siredvin.tweakium.modules.platform

import dan200.computercraft.api.pocket.IPocketUpgrade
import dan200.computercraft.api.pocket.PocketUpgradeSerialiser
import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser
import net.minecraft.resources.ResourceLocation
import site.siredvin.broccolium.modules.platform.BasePlatform
import site.siredvin.tweakium.modules.data.ComputerModInformationHolder
import site.siredvin.tweakium.modules.platform.api.InnerComputerBasePlatform
import java.util.function.Supplier

abstract class ComputerBasePlatform:
    BasePlatform() {
    abstract override val baseInnerPlatform: InnerComputerBasePlatform
    abstract override val modInformationTracker: ComputerModInformationTracker

    @Suppress("UNCHECKED_CAST")
    override val holder: ComputerModInformationHolder
        get() = modInformationTracker

    fun <V : ITurtleUpgrade> registerTurtleUpgrade(
        name: String,
        serializer: TurtleUpgradeSerialiser<V>,
    ): Supplier<TurtleUpgradeSerialiser<V>> {
        return registerTurtleUpgrade(ResourceLocation(baseInnerPlatform.modID, name), serializer)
    }

    fun <V : ITurtleUpgrade> registerTurtleUpgrade(
        key: ResourceLocation,
        serializer: TurtleUpgradeSerialiser<V>,
    ): Supplier<TurtleUpgradeSerialiser<V>> {
        val registered = baseInnerPlatform.registerTurtleUpgrade(key, serializer)
        @Suppress("UNCHECKED_CAST")
        modInformationTracker.internalTurtleUpgrades.add(registered as Supplier<TurtleUpgradeSerialiser<out ITurtleUpgrade>>)
        return registered
    }

    fun <V : IPocketUpgrade> registerPocketUpgrade(
        name: String,
        serializer: PocketUpgradeSerialiser<V>,
    ): Supplier<PocketUpgradeSerialiser<V>> {
        return registerPocketUpgrade(ResourceLocation(baseInnerPlatform.modID, name), serializer)
    }

    fun <V : IPocketUpgrade> registerPocketUpgrade(
        key: ResourceLocation,
        serializer: PocketUpgradeSerialiser<V>,
    ): Supplier<PocketUpgradeSerialiser<V>> {
        val registered = baseInnerPlatform.registerPocketUpgrade(key, serializer)
        @Suppress("UNCHECKED_CAST")
        modInformationTracker.internalPocketUpgrades.add(registered as Supplier<PocketUpgradeSerialiser<out IPocketUpgrade>>)
        return registered
    }
}