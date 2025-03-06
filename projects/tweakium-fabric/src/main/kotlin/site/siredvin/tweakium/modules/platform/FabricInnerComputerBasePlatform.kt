package site.siredvin.tweakium.modules.platform

import dan200.computercraft.api.pocket.IPocketUpgrade
import dan200.computercraft.api.turtle.ITurtleUpgrade
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
        upgrade: V,
    ): Supplier<V> {
        @Suppress("UNCHECKED_CAST")
        val registry: Registry<ITurtleUpgrade> = (
            BuiltInRegistries.REGISTRY.get(ITurtleUpgrade.REGISTRY.location())
                ?: throw IllegalStateException("Something is not correct with turtle registry")
            ) as Registry<ITurtleUpgrade>
        val registered = Registry.register(registry, key, upgrade)
        return Supplier { registered }
    }

    override fun <V : IPocketUpgrade> registerPocketUpgrade(
        key: ResourceLocation,
        upgrade: V,
    ): Supplier<V> {
        @Suppress("UNCHECKED_CAST")
        val registry: Registry<IPocketUpgrade> = (
            BuiltInRegistries.REGISTRY.get(IPocketUpgrade.REGISTRY.location())
                ?: throw IllegalStateException("Something is not correct with turtle registry")
            ) as Registry<IPocketUpgrade>
        val registered = Registry.register(registry, key, upgrade)
        return Supplier { registered }
    }
}
