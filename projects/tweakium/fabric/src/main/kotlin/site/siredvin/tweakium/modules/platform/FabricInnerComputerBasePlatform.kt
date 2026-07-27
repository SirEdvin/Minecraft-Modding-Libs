package site.siredvin.tweakium.modules.platform

import dan200.computercraft.api.pocket.IPocketUpgrade
import dan200.computercraft.api.pocket.PocketUpgradeSerialiser
import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import site.siredvin.broccolium.modules.platform.FabricInnerBasePlatform
import site.siredvin.tweakium.modules.platform.api.InnerComputerBasePlatform
import java.util.function.Supplier

abstract class FabricInnerComputerBasePlatform :
    FabricInnerBasePlatform(),
    InnerComputerBasePlatform {

    override fun <V : ITurtleUpgrade> registerTurtleUpgrade(
        key: ResourceLocation,
        serializer: TurtleUpgradeSerialiser<V>,
    ): Supplier<TurtleUpgradeSerialiser<V>> {
        @Suppress("UNCHECKED_CAST")
        val registry: Registry<TurtleUpgradeSerialiser<*>> = (
            BuiltInRegistries.REGISTRY.get(TurtleUpgradeSerialiser.registryId().location())
                ?: throw IllegalStateException("Something is not correct with turtle registry")
            ) as Registry<TurtleUpgradeSerialiser<*>>
        val registered = Registry.register(registry, key, serializer)
        return Supplier { registered }
    }

    override fun <V : IPocketUpgrade> registerPocketUpgrade(
        key: ResourceLocation,
        serializer: PocketUpgradeSerialiser<V>,
    ): Supplier<PocketUpgradeSerialiser<V>> {
        @Suppress("UNCHECKED_CAST")
        val registry: Registry<PocketUpgradeSerialiser<*>> = (
            BuiltInRegistries.REGISTRY.get(PocketUpgradeSerialiser.registryId().location())
                ?: throw IllegalStateException("Something is not correct with turtle registry")
            ) as Registry<PocketUpgradeSerialiser<*>>
        val registered = Registry.register(registry, key, serializer)
        return Supplier { registered }
    }
}
