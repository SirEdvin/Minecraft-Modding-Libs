package site.siredvin.broccolium.modules.storage.energy

import site.siredvin.broccolium.modules.data.BroccoliumText

object Energies {
    val TURTLE_FUEL = EnergyRegistry.register("turtleFuel", BroccoliumText.TURTLE_FUEL_ENERGY.text)
    val FORGE = EnergyRegistry.register("FE", BroccoliumText.FORGE_ENERGY.text)
    val REDSTONE_FLUX = EnergyRegistry.register("RF", BroccoliumText.RF_ENERGY.text)
}
