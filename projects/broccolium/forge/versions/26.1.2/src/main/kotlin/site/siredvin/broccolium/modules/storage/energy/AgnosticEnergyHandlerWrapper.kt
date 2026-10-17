package site.siredvin.broccolium.modules.storage.energy

import net.neoforged.neoforge.transfer.energy.EnergyHandler
import net.neoforged.neoforge.transfer.transaction.Transaction
import site.siredvin.broccolium.modules.storage.base.api.SomethingOperator
import site.siredvin.broccolium.modules.storage.energy.api.AgnosticEnergyStorage
import java.util.function.Predicate

class AgnosticEnergyHandlerWrapper(private val handler: EnergyHandler) : AgnosticEnergyStorage {
    override fun getContent(): Iterator<AgnosticEnergyStack> = listOf(firstEnergy).iterator()
    override val firstEnergy: AgnosticEnergyStack
        get() = AgnosticEnergyStack(Energies.FORGE, handler.amountAsLong)

    override fun take(predicate: Predicate<AgnosticEnergyStack>, limit: Long, simulate: Boolean): AgnosticEnergyStack {
        require(limit >= 0) { "Negative energy limit" }
        if (limit == 0L || !predicate.test(firstEnergy)) return AgnosticEnergyStack(Energies.FORGE, 0)
        Transaction.openRoot().use { transaction ->
            val extracted = handler.extract(limit.coerceAtMost(Int.MAX_VALUE.toLong()).toInt(), transaction)
            if (!simulate) transaction.commit()
            return AgnosticEnergyStack(Energies.FORGE, extracted.toLong())
        }
    }

    override val canExtract: Boolean
        get() = Transaction.openRoot().use { handler.extract(1, it) > 0 }
    override val canReceive: Boolean
        get() = Transaction.openRoot().use { handler.insert(1, it) > 0 }
    override val maxStackSize: Long
        get() = handler.capacityAsLong
    override val operator: SomethingOperator<AgnosticEnergyStack, Long>
        get() = EnergyStorageUtils

    override fun store(stack: AgnosticEnergyStack, simulate: Boolean): AgnosticEnergyStack {
        if (!stack.`is`(Energies.FORGE) || stack.amount <= 0) return stack.copy()
        Transaction.openRoot().use { transaction ->
            val inserted = handler.insert(stack.amount.coerceAtMost(Int.MAX_VALUE.toLong()).toInt(), transaction)
            if (!simulate) transaction.commit()
            return AgnosticEnergyStack(Energies.FORGE, stack.amount - inserted)
        }
    }

    override fun setChanged() {
    }
}
