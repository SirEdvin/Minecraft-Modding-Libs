package site.siredvin.tweakium.modules.platform

import dan200.computercraft.api.pocket.IPocketUpgrade
import dan200.computercraft.api.pocket.PocketUpgradeSerialiser
import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser
import site.siredvin.broccolium.modules.platform.ModInformationTracker
import site.siredvin.tweakium.modules.data.ComputerModInformationHolder
import java.util.function.Supplier

open class ComputerModInformationTracker :
    ModInformationTracker(),
    ComputerModInformationHolder {
    val internalPocketUpgrades: MutableList<Supplier<PocketUpgradeSerialiser<out IPocketUpgrade>>> = mutableListOf()
    val internalTurtleUpgrades: MutableList<Supplier<TurtleUpgradeSerialiser<out ITurtleUpgrade>>> = mutableListOf()
    override val pocketSerializers: List<Supplier<PocketUpgradeSerialiser<out IPocketUpgrade>>>
        get() = internalPocketUpgrades
    override val turtleSerializers: List<Supplier<TurtleUpgradeSerialiser<out ITurtleUpgrade>>>
        get() = internalTurtleUpgrades
}
