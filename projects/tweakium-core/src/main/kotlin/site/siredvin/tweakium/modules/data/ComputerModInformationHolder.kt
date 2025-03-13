package site.siredvin.tweakium.modules.data

import dan200.computercraft.api.pocket.IPocketUpgrade
import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.api.upgrades.UpgradeType
import site.siredvin.broccolium.modules.data.api.ModInformationHolder
import site.siredvin.broccolium.modules.platform.api.RegistryEntry

interface ComputerModInformationHolder : ModInformationHolder {
    val turtleUpgrades: List<RegistryEntry<UpgradeType<out ITurtleUpgrade>>>
        get() = emptyList()
    val pocketUpgrades: List<RegistryEntry<UpgradeType<out IPocketUpgrade>>>
        get() = emptyList()
}
