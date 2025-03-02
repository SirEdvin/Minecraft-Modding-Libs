package site.siredvin.tweakium.modules.peripheral.ability

import site.siredvin.tweakium.modules.peripheral.owner.TurtlePeripheralOwner

open class TurtleFuelBoon(owner: TurtlePeripheralOwner, override val maxFuelConsumptionRate: Int) : FuelBoon<TurtlePeripheralOwner>(owner) {

    override fun consumeFuelInternal(count: Int): Boolean = owner.turtle.consumeFuel(count)

    override val isFuelConsumptionDisable: Boolean
        get() = !owner.turtle.isFuelNeeded
    override val fuelCount: Int
        get() = owner.turtle.fuelLevel
    override val fuelMaxCount: Int
        get() = owner.turtle.fuelLimit

    override fun addFuel(count: Int) {
        owner.turtle.addFuel(count)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TurtleFuelBoon) return false
        if (!super.equals(other)) return false

        if (maxFuelConsumptionRate != other.maxFuelConsumptionRate) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + maxFuelConsumptionRate
        return result
    }
}
