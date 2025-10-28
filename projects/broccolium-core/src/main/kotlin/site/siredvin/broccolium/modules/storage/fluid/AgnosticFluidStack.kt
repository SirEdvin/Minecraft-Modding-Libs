package site.siredvin.broccolium.modules.storage.fluid

import net.minecraft.core.component.DataComponentPatch
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
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
            if (!isSameFluid(first, second)) {
                return false
            }
            return first.components == second.components
        }

        fun of(targetTag: CompoundTag): AgnosticFluidStack {
            val fluidID = targetTag.getString("fluid")
            val fluid = PlatformRegistries.FLUIDS.tryGet(ResourceLocation.parse(fluidID)) ?: return EMPTY
            val amount = targetTag.getDouble("amount")
            if (amount == 0.0) {
                return EMPTY
            }
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
        get() = fluid.isSame(Fluids.EMPTY)

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

    fun save(targetTag: CompoundTag): CompoundTag {
        targetTag.putString("fluid", PlatformRegistries.FLUIDS.getKey(fluid).toString())
        targetTag.putDouble("amount", amount)
        if (!components.isEmpty) {
            val potentialTag = DataComponentUtil.patchToNBT(components)
            if (potentialTag != null) {
                targetTag.put("tag", potentialTag)
            }
        }
        return targetTag
    }
}
