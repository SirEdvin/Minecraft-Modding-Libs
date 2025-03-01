package site.siredvin.broccolium.modules.storage.energy

import site.siredvin.broccolium.modules.data.BroccoliumText

object Energies {
    val EMPTY = EnergyUnit("empty", BroccoliumText.EMPTY_ENERGY.text)
    val TURTLE_FUEL = EnergyUnit("turtleFuel", BroccoliumText.TURTLE_FUEL_ENERGY.text)
    val FORGE = EnergyUnit("FE", BroccoliumText.FORGE_ENERGY.text)
    val REDSTONE_FLUX = EnergyUnit("RF", BroccoliumText.RF_ENERGY.text)
}
