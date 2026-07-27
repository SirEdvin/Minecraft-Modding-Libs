package site.siredvin.tweakium.modules.platform

import dan200.computercraft.api.pocket.IPocketUpgrade
import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.api.upgrades.UpgradeType
import net.minecraft.resources.ResourceLocation
import site.siredvin.broccolium.modules.platform.ForgeInnerBasePlatform
import site.siredvin.tweakium.ForgeTweakium
import site.siredvin.tweakium.modules.platform.api.InnerComputerBasePlatform
import java.util.function.Supplier

abstract class ForgeInnerComputerBasePlatform :
    ForgeInnerBasePlatform(),
    InnerComputerBasePlatform {

    override fun <V : ITurtleUpgrade> registerTurtleUpgrade(
        key: ResourceLocation,
        upgrade: UpgradeType<V>,
    ): Supplier<UpgradeType<V>> = ForgeTweakium.turtleUpgrades.register(key.path, Supplier { upgrade })

    override fun <V : IPocketUpgrade> registerPocketUpgrade(
        key: ResourceLocation,
        upgrade: UpgradeType<V>,
    ): Supplier<UpgradeType<V>> = ForgeTweakium.pocketUpgrades.register(key.path, Supplier { upgrade })
}
