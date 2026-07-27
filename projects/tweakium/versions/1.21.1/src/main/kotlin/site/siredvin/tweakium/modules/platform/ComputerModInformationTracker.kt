package site.siredvin.tweakium.modules.platform

import dan200.computercraft.api.pocket.IPocketUpgrade
import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.api.upgrades.UpgradeType
import site.siredvin.broccolium.modules.platform.ModInformationTracker
import site.siredvin.broccolium.modules.platform.api.RegistryEntry
import site.siredvin.tweakium.modules.data.ComputerModInformationHolder

open class ComputerModInformationTracker :
    ModInformationTracker(),
    ComputerModInformationHolder {
    val internalPocketUpgrades: MutableList<RegistryEntry<UpgradeType<out IPocketUpgrade>>> = mutableListOf()
    val internalTurtleUpgrades: MutableList<RegistryEntry<UpgradeType<out ITurtleUpgrade>>> = mutableListOf()

    override val pocketUpgrades: List<RegistryEntry<UpgradeType<out IPocketUpgrade>>>
        get() = internalPocketUpgrades
    override val turtleUpgrades: List<RegistryEntry<UpgradeType<out ITurtleUpgrade>>>
        get() = internalTurtleUpgrades
}
