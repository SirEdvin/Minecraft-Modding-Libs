package site.siredvin.tweakium.modules.data

import dan200.computercraft.api.pocket.IPocketUpgrade
import dan200.computercraft.api.turtle.ITurtleUpgrade
import site.siredvin.broccolium.modules.data.api.ModInformationHolder
import site.siredvin.broccolium.modules.platform.api.RegistryEntry

interface ComputerModInformationHolder : ModInformationHolder {
    val turtleUpgrades: List<RegistryEntry<out ITurtleUpgrade>>
        get() = emptyList()
    val pocketUpgrades: List<RegistryEntry<out IPocketUpgrade>>
        get() = emptyList()
}
