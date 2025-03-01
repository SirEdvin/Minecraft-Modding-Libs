package site.siredvin.tweakium.modules.data

import dan200.computercraft.api.pocket.IPocketUpgrade
import dan200.computercraft.api.pocket.PocketUpgradeSerialiser
import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser
import site.siredvin.broccolium.modules.data.api.ModInformationHolder
import java.util.function.Supplier

interface ComputerModInformationHolder: ModInformationHolder {
    val turtleSerializers: List<Supplier<TurtleUpgradeSerialiser<out ITurtleUpgrade>>>
        get() = emptyList()
    val pocketSerializers: List<Supplier<PocketUpgradeSerialiser<out IPocketUpgrade>>>
        get() = emptyList()
}