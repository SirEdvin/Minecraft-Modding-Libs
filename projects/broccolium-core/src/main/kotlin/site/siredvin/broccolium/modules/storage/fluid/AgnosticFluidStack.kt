package site.siredvin.broccolium.modules.storage.fluid

import net.minecraft.core.component.DataComponentPatch
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.material.Fluids
import site.siredvin.broccolium.modules.platform.PlatformToolkit

data class AgnosticFluidStack(val fluid: Fluid, var amount: Long, var components: DataComponentPatch = DataComponentPatch.EMPTY) {
    companion object {
        val EMPTY = AgnosticFluidStack(Fluids.EMPTY, 0)
        fun isSameFluid(first: AgnosticFluidStack, second: AgnosticFluidStack): Boolean = first.fluid.isSame(second.fluid)

        fun isSameFluidSameTags(first: AgnosticFluidStack, second: AgnosticFluidStack): Boolean {
            if (!isSameFluid(first, second)) {
                return false
            }
            return first.components == second.components
        }
    }
    val isEmpty: Boolean
        get() = fluid.isSame(Fluids.EMPTY)

    val platformAmount: Long
        get() = this.amount * PlatformToolkit.get().fluidCompactDivider

    fun copy(): AgnosticFluidStack = AgnosticFluidStack(fluid, amount, components)

    fun copyWithCount(count: Long): AgnosticFluidStack = AgnosticFluidStack(fluid, count, components)

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
}
