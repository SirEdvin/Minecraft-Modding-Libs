package site.siredvin.broccolium.modules.storage.energy

import net.minecraft.nbt.CompoundTag

data class AgnosticEnergyStack(val unit: EnergyUnit, var amount: Long) {
    companion object {
        fun isSameEnergy(first: AgnosticEnergyStack, second: AgnosticEnergyStack): Boolean = first.unit == second.unit

        fun of(targetTag: CompoundTag): AgnosticEnergyStack {
            val energyId = targetTag.getStringOr("energy", "")
            val energy = EnergyRegistry.ENERGIES[energyId]!!
            val amount = targetTag.getLongOr("amount", 0L)
            return AgnosticEnergyStack(energy, amount)
        }
    }
    val isEmpty: Boolean
        get() = amount == 0L

    fun copy(): AgnosticEnergyStack = AgnosticEnergyStack(unit, amount)

    fun `is`(unit: EnergyUnit): Boolean = unit == this.unit

    fun copyWithCount(count: Long): AgnosticEnergyStack = AgnosticEnergyStack(unit, count)

    fun grow(amount: Int) {
        this.amount += amount.toLong()
    }

    fun shrink(amount: Int) {
        this.amount -= amount.toLong()
    }

    fun grow(amount: Long) {
        this.amount += amount
    }

    fun shrink(amount: Long) {
        this.amount -= amount
    }

    fun split(amount: Long): AgnosticEnergyStack {
        if (this.amount <= amount) {
            val fullStack = this.copy()
            this.amount = 0
            return fullStack
        }
        this.shrink(amount)
        return this.copyWithCount(amount)
    }

    fun save(targetTag: CompoundTag): CompoundTag {
        targetTag.putString("energy", unit.name)
        targetTag.putLong("amount", amount)
        return targetTag
    }
}
