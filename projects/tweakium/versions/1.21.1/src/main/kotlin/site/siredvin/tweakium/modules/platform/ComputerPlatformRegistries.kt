package site.siredvin.tweakium.modules.platform

import dan200.computercraft.api.pocket.IPocketUpgrade
import dan200.computercraft.api.turtle.ITurtleUpgrade
import site.siredvin.broccolium.modules.platform.PlatformToolkit

object ComputerPlatformRegistries {
    val POCKET_UPGRADES = PlatformToolkit.get().lookup(IPocketUpgrade.REGISTRY)
    val TURTLE_UPGRADES = PlatformToolkit.get().lookup(ITurtleUpgrade.REGISTRY)
}
