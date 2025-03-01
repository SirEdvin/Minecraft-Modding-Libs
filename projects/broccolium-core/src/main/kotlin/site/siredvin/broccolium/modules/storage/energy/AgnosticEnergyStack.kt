package site.siredvin.broccolium.modules.storage.energy

data class AgnosticEnergyStack(val unit: EnergyUnit, var amount: Long) {
    companion object {
        val EMPTY = AgnosticEnergyStack(Energies.EMPTY, 0)
        fun isSameEnergy(first: AgnosticEnergyStack, second: AgnosticEnergyStack): Boolean = first.unit == second.unit
    }
    val isEmpty: Boolean
        get() = unit == Energies.EMPTY

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
}
