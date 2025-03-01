package site.siredvin.broccolium.modules.storage.fluid

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.material.Fluids
import site.siredvin.broccolium.modules.platform.PlatformToolkit

data class AgnosticFluidStack(val fluid: Fluid, var amount: Long, var tag: CompoundTag? = null) {
    companion object {
        val EMPTY = AgnosticFluidStack(Fluids.EMPTY, 0)
        fun isSameFluid(first: AgnosticFluidStack, second: AgnosticFluidStack): Boolean = first.fluid.isSame(second.fluid)

        fun isSameFluidSameTags(first: AgnosticFluidStack, second: AgnosticFluidStack): Boolean {
            if (!isSameFluid(first, second)) {
                return false
            }
            return first.tag == second.tag
        }
    }
    val isEmpty: Boolean
        get() = fluid.isSame(Fluids.EMPTY)

    val platformAmount: Long
        get() = this.amount * PlatformToolkit.get().fluidCompactDivider

    fun copy(): AgnosticFluidStack = AgnosticFluidStack(fluid, amount, tag?.copy())

    fun copyWithCount(count: Long): AgnosticFluidStack = AgnosticFluidStack(fluid, count, tag?.copy())

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
