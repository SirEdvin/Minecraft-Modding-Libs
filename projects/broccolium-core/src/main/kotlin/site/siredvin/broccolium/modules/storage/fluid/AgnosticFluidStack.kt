package site.siredvin.broccolium.modules.storage.fluid

import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.material.Fluids
import site.siredvin.broccolium.modules.platform.PlatformRegistries
import site.siredvin.broccolium.modules.platform.PlatformToolkit

data class AgnosticFluidStack(val fluid: Fluid, var amount: Double, var tag: CompoundTag? = null) {
    companion object {
        val EMPTY = AgnosticFluidStack(Fluids.EMPTY, 0.0)
        fun isSameFluid(first: AgnosticFluidStack, second: AgnosticFluidStack): Boolean = first.fluid.isSame(second.fluid)

        fun isSameFluidSameTags(first: AgnosticFluidStack, second: AgnosticFluidStack): Boolean {
            if (!isSameFluid(first, second)) {
                return false
            }
            return first.tag == second.tag
        }

        fun of(targetTag: CompoundTag): AgnosticFluidStack {
            val fluidID = targetTag.getString("fluid")
            val fluid = PlatformRegistries.FLUIDS.tryGet(ResourceLocation(fluidID)) ?: return EMPTY
            val amount = targetTag.getDouble("amount")
            if (amount == 0.0) {
                return EMPTY
            }
            return AgnosticFluidStack(
                fluid,
                amount,
                if (targetTag.contains("tag")) {
                    targetTag.getCompound("tag")
                } else {
                    null
                },
            )
        }
    }
    val isEmpty: Boolean
        get() = fluid.isSame(Fluids.EMPTY)

    val platformAmount: Double
        get() = this.amount * PlatformToolkit.get().fluidCompactDivider

    fun copy(): AgnosticFluidStack = AgnosticFluidStack(fluid, amount, tag?.copy())

    fun copyWithCount(count: Double): AgnosticFluidStack = AgnosticFluidStack(fluid, count, tag?.copy())

    fun grow(amount: Double) {
        this.amount += amount
    }

    fun shrink(amount: Double) {
        this.amount -= amount
    }

    fun save(targetTag: CompoundTag): CompoundTag {
        targetTag.putString("fluid", PlatformRegistries.FLUIDS.getKey(fluid).toString())
        targetTag.putDouble("amount", amount)
        if (tag != null) {
            targetTag.put("tag", tag!!)
        }
        return targetTag
    }
}
