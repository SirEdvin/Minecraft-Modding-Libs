package site.siredvin.broccolium.modules.storage.fluid

import net.neoforged.neoforge.fluids.FluidStack

fun FluidStack.toVanilla(): AgnosticFluidStack {
    if (this.isEmpty) return AgnosticFluidStack.EMPTY
    return AgnosticFluidStack(this.fluid, this.amount.toDouble(), this.components.asPatch())
}

fun FluidStack.copyWithCount(count: Int): FluidStack {
    val copy = this.copy()
    copy.amount = count
    return copy
}

fun AgnosticFluidStack.toForge(): FluidStack {
    if (this.isEmpty) return FluidStack.EMPTY
    @Suppress("DEPRECATION")
    return FluidStack(this.fluid.builtInRegistryHolder(), this.amount.toInt(), this.components)
}
