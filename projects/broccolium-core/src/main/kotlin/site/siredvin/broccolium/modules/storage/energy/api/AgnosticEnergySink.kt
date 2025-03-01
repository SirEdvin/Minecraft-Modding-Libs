package site.siredvin.broccolium.modules.storage.energy.api

import site.siredvin.broccolium.modules.storage.energy.AgnosticEnergyStack
import site.siredvin.broccolium.modules.storage.energy.EnergyStorageUtils
import java.util.function.Predicate

interface AgnosticEnergySink {
    fun moveFrom(from: AgnosticEnergyStorage, limit: Long, takePredicate: Predicate<AgnosticEnergyStack>): Long {
        if (movableType != null) {
            throw IllegalStateException("With movable type you should redefine this function")
        }
        if (from.movableType == null) {
            return EnergyStorageUtils.naiveMove(from, this, limit, takePredicate)
        }
        return from.moveTo(this, limit, takePredicate)
    }
    fun storeEnergy(stack: AgnosticEnergyStack): AgnosticEnergyStack
    fun setChanged()

    val movableType: String?
        get() = null
}
