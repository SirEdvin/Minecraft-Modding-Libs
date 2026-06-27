package site.siredvin.tweakium.modules.storage.energy

import dan200.computercraft.api.turtle.ITurtleAccess
import dan200.computercraft.api.turtle.TurtleAnimation
import site.siredvin.broccolium.modules.storage.base.api.SomethingOperator
import site.siredvin.broccolium.modules.storage.energy.AgnosticEnergyStack
import site.siredvin.broccolium.modules.storage.energy.Energies
import site.siredvin.broccolium.modules.storage.energy.EnergyStorageUtils
import site.siredvin.broccolium.modules.storage.energy.api.AgnosticEnergyStorage
import java.util.function.Predicate

class TurtleAgnosticEnergyStorage(private val turtle: ITurtleAccess) : AgnosticEnergyStorage {
    override val firstEnergy: AgnosticEnergyStack
        get() = AgnosticEnergyStack(Energies.TURTLE_FUEL, turtle.fuelLevel.toLong())

    override val maxStackSize: Long
        get() = turtle.fuelLimit.toLong()
    override val operator: SomethingOperator<AgnosticEnergyStack, Long>
        get() = EnergyStorageUtils

    override fun getContent(): Iterator<AgnosticEnergyStack> = listOf(firstEnergy).iterator()

    override fun take(predicate: Predicate<AgnosticEnergyStack>, limit: Long, simulate: Boolean): AgnosticEnergyStack {
        if (!predicate.test(firstEnergy)) return AgnosticEnergyStack(Energies.TURTLE_FUEL, 0)
        val extractedEnergy = minOf(limit, turtle.fuelLevel.toLong())
        if (!simulate) {
            turtle.consumeFuel(extractedEnergy.toInt())
        }
        return AgnosticEnergyStack(Energies.TURTLE_FUEL, extractedEnergy)
    }

    override val canExtract: Boolean
        get() = true

    override fun store(stack: AgnosticEnergyStack, simulate: Boolean): AgnosticEnergyStack {
        if (!stack.`is`(Energies.TURTLE_FUEL)) return stack
        val insertedEnergy = minOf(stack.amount, turtle.fuelLimit.toLong() - turtle.fuelLevel.toLong())
        if (!simulate) {
            turtle.addFuel(insertedEnergy.toInt())
        }
        stack.shrink(insertedEnergy)
        return stack
    }

    override fun setChanged() {
        turtle.playAnimation(TurtleAnimation.NONE)
    }

    override val canReceive: Boolean
        get() = true
}
