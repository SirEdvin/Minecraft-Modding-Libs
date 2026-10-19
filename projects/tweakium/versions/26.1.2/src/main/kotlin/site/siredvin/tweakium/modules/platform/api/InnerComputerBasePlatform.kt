package site.siredvin.tweakium.modules.platform.api

import dan200.computercraft.api.pocket.IPocketUpgrade
import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.api.upgrades.UpgradeType
import net.minecraft.resources.Identifier
import site.siredvin.broccolium.modules.platform.api.InnerBasePlatform
import java.util.function.Supplier

interface InnerComputerBasePlatform : InnerBasePlatform {
    fun <V : ITurtleUpgrade> registerTurtleUpgrade(
        key: Identifier,
        upgrade: UpgradeType<V>,
    ): Supplier<UpgradeType<V>>
    fun <V : IPocketUpgrade> registerPocketUpgrade(
        key: Identifier,
        upgrade: UpgradeType<V>,
    ): Supplier<UpgradeType<V>>
}
