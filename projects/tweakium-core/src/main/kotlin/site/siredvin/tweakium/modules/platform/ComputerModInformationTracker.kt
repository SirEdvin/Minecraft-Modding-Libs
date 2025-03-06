package site.siredvin.tweakium.modules.platform

import dan200.computercraft.api.pocket.IPocketUpgrade
import dan200.computercraft.api.turtle.ITurtleUpgrade
import site.siredvin.broccolium.modules.platform.ModInformationTracker
import site.siredvin.broccolium.modules.platform.api.RegistryEntry
import site.siredvin.tweakium.modules.data.ComputerModInformationHolder

open class ComputerModInformationTracker :
    ModInformationTracker(),
    ComputerModInformationHolder {
    val internalPocketUpgrades: MutableList<RegistryEntry<out IPocketUpgrade>> = mutableListOf()
    val internalTurtleUpgrades: MutableList<RegistryEntry<out ITurtleUpgrade>> = mutableListOf()

    override val pocketUpgrades: List<RegistryEntry<out IPocketUpgrade>>
        get() = internalPocketUpgrades
    override val turtleUpgrades: List<RegistryEntry<out ITurtleUpgrade>>
        get() = internalTurtleUpgrades
}
