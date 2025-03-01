package site.siredvin.tweakium.modules.platform

import dan200.computercraft.api.pocket.PocketUpgradeSerialiser
import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser
import site.siredvin.sortingmess.xplat.PeripheraliumPlatform

object ComputerPlatformRegistries {
    val TURTLE_SERIALIZERS by lazy { PeripheraliumPlatform.wrap(TurtleUpgradeSerialiser.registryId()) }
    val POCKET_SERIALIZERS by lazy { PeripheraliumPlatform.wrap(PocketUpgradeSerialiser.registryId()) }
}
