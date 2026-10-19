package site.siredvin.tweakium.modules.platform

import dan200.computercraft.api.pocket.IPocketUpgrade
import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.api.upgrades.UpgradeType
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier
import site.siredvin.broccolium.modules.platform.FabricInnerBasePlatform
import site.siredvin.tweakium.modules.platform.api.InnerComputerBasePlatform
import java.util.function.Supplier

abstract class FabricInnerComputerBasePlatform :
    FabricInnerBasePlatform(),
    InnerComputerBasePlatform {
    override fun <V : ITurtleUpgrade> registerTurtleUpgrade(key: Identifier, upgrade: UpgradeType<V>): Supplier<UpgradeType<V>> {
        @Suppress("UNCHECKED_CAST")
        val registry = BuiltInRegistries.REGISTRY.getValue(ITurtleUpgrade.typeRegistry().identifier()) as Registry<UpgradeType<ITurtleUpgrade>>

        @Suppress("UNCHECKED_CAST")
        val registered = Registry.register(registry, key, upgrade as UpgradeType<ITurtleUpgrade>) as UpgradeType<V>
        return Supplier { registered }
    }

    override fun <V : IPocketUpgrade> registerPocketUpgrade(key: Identifier, upgrade: UpgradeType<V>): Supplier<UpgradeType<V>> {
        @Suppress("UNCHECKED_CAST")
        val registry = BuiltInRegistries.REGISTRY.getValue(IPocketUpgrade.typeRegistry().identifier()) as Registry<UpgradeType<IPocketUpgrade>>

        @Suppress("UNCHECKED_CAST")
        val registered = Registry.register(registry, key, upgrade as UpgradeType<IPocketUpgrade>) as UpgradeType<V>
        return Supplier { registered }
    }
}
