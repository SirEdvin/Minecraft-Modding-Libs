package site.siredvin.broccolium.modules.storage.fluid

import net.minecraft.core.component.DataComponentPatch
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.Identifier
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.material.Fluids
import site.siredvin.broccolium.modules.base.util.DataComponentUtil
import site.siredvin.broccolium.modules.platform.PlatformRegistries
import site.siredvin.broccolium.modules.platform.PlatformToolkit

data class AgnosticFluidStack(val fluid: Fluid, var amount: Double, var components: DataComponentPatch = DataComponentPatch.EMPTY) {
    companion object {
        val EMPTY = AgnosticFluidStack(Fluids.EMPTY, 0.0)
        fun isSameFluid(first: AgnosticFluidStack, second: AgnosticFluidStack): Boolean = first.fluid.isSame(second.fluid)

        fun isSameFluidSameTags(first: AgnosticFluidStack, second: AgnosticFluidStack): Boolean {
            if (!isSameFluid(first, second)) return false
            return first.components == second.components
        }

        fun of(targetTag: CompoundTag): AgnosticFluidStack {
            val fluidID = targetTag.getStringOr("fluid", "")
            val fluid = PlatformRegistries.FLUIDS.tryGet(Identifier.parse(fluidID)) ?: return EMPTY
            val amount = targetTag.getDoubleOr("amount", 0.0)
            if (amount == 0.0) return EMPTY
            return AgnosticFluidStack(
                fluid,
                amount,
                if (targetTag.contains("tag")) {
                    DataComponentUtil.nbtToPatch(targetTag.get("tag")) ?: DataComponentPatch.EMPTY
                } else {
                    DataComponentPatch.EMPTY
                },
            )
        }
    }
    val isEmpty: Boolean
        get() = fluid.isSame(Fluids.EMPTY) || amount == 0.0

    val platformAmount: Double
        get() = this.amount * PlatformToolkit.get().fluidCompactDivider

    fun copy(): AgnosticFluidStack = AgnosticFluidStack(fluid, amount, components)

    fun copyWithCount(count: Double): AgnosticFluidStack = AgnosticFluidStack(fluid, count, components)

    fun grow(amount: Double) {
        this.amount += amount
    }

    fun shrink(amount: Double) {
        this.amount -= amount
    }

    fun split(splitAmount: Double): AgnosticFluidStack {
        val realAmount = splitAmount.coerceAtMost(amount)
        val newStack = this.copyWithCount(realAmount)
        this.shrink(realAmount)
        return newStack
    }

    fun save(targetTag: CompoundTag): CompoundTag {
        val fluidId = PlatformRegistries.FLUIDS.getKey(fluid).toString()
        val encodedComponents = if (components.isEmpty) null else DataComponentUtil.patchToNBT(components)
        targetTag.putString("fluid", fluidId)
        targetTag.putDouble("amount", amount)
        if (encodedComponents != null) targetTag.put("tag", encodedComponents)
        return targetTag
    }
}
