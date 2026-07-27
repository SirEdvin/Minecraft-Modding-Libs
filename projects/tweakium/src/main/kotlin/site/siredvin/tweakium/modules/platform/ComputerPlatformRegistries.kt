package site.siredvin.tweakium.modules.platform

import dan200.computercraft.api.pocket.PocketUpgradeSerialiser
import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser
import site.siredvin.broccolium.modules.platform.PlatformToolkit

object ComputerPlatformRegistries {
    val TURTLE_SERIALIZERS by lazy { PlatformToolkit.get().wrap(TurtleUpgradeSerialiser.registryId()) }
    val POCKET_SERIALIZERS by lazy { PlatformToolkit.get().wrap(PocketUpgradeSerialiser.registryId()) }
}
