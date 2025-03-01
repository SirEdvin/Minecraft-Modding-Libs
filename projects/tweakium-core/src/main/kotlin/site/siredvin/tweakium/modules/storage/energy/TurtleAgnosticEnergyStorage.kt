package site.siredvin.tweakium.modules.storage.energy

import dan200.computercraft.api.turtle.ITurtleAccess
import dan200.computercraft.api.turtle.TurtleAnimation
import site.siredvin.broccolium.modules.storage.energy.Energies
import site.siredvin.broccolium.modules.storage.energy.AgnosticEnergyStack
import site.siredvin.broccolium.modules.storage.energy.api.AgnosticEnergyStorage
import java.util.function.Predicate

class TurtleAgnosticEnergyStorage(private val turtle: ITurtleAccess) : AgnosticEnergyStorage {
    override val energy: AgnosticEnergyStack
        get() = AgnosticEnergyStack(Energies.TURTLE_FUEL, turtle.fuelLevel.toLong())

    override val capacity: Long
        get() = turtle.fuelLimit.toLong()

    override fun takeEnergy(predicate: Predicate<AgnosticEnergyStack>, limit: Long): AgnosticEnergyStack {
        if (!predicate.test(energy)) return AgnosticEnergyStack.EMPTY
        val extractedEnergy = minOf(limit, turtle.fuelLevel.toLong())
        turtle.addFuel(-extractedEnergy.toInt())
        return AgnosticEnergyStack(Energies.TURTLE_FUEL, extractedEnergy)
    }

    override fun storeEnergy(stack: AgnosticEnergyStack): AgnosticEnergyStack {
        if (!stack.`is`(Energies.TURTLE_FUEL)) return stack
        val insertedEnergy = minOf(stack.amount, turtle.fuelLimit.toLong() - turtle.fuelLevel.toLong())
        turtle.addFuel(insertedEnergy.toInt())
        stack.shrink(insertedEnergy)
        if (stack.amount == 0L) return AgnosticEnergyStack.EMPTY
        return stack
    }

    override fun setChanged() {
        turtle.playAnimation(TurtleAnimation.NONE)
    }
}
