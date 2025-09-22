package site.siredvin.broccolium.modules.storage.energy.api

import site.siredvin.broccolium.modules.storage.energy.AgnosticEnergyStack
import site.siredvin.broccolium.modules.storage.energy.EnergyStorageUtils
import java.util.function.Predicate

interface AgnosticEnergyStorage : AgnosticEnergySink {
    val energy: AgnosticEnergyStack
    val capacity: Long
    fun takeEnergy(predicate: Predicate<AgnosticEnergyStack>, limit: Long): AgnosticEnergyStack

    val canExtract: Boolean

    fun moveTo(to: AgnosticEnergySink, limit: Long, takePredicate: Predicate<AgnosticEnergyStack>): Long {
        if (movableType != null) {
            throw IllegalStateException("With movable type you should redefine this function")
        }
        if (to.movableType == null) {
            return EnergyStorageUtils.naiveMove(this, to, limit, takePredicate)
        }
        return to.moveFrom(this, limit, takePredicate)
    }
}
