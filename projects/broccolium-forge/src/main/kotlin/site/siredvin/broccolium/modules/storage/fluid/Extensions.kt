package site.siredvin.broccolium.modules.storage.fluid

import net.minecraftforge.fluids.FluidStack as ForgeFluidStack

fun ForgeFluidStack.toVanilla(): AgnosticFluidStack {
    if (this.isEmpty) return AgnosticFluidStack.EMPTY
    return AgnosticFluidStack(this.fluid, this.amount.toDouble(), this.tag)
}

fun ForgeFluidStack.copyWithCount(count: Int): ForgeFluidStack {
    val copy = this.copy()
    copy.amount = count
    return copy
}

fun AgnosticFluidStack.toForge(): ForgeFluidStack {
    if (this.isEmpty) return ForgeFluidStack.EMPTY
    return ForgeFluidStack(this.fluid, this.amount.toInt(), this.tag)
}
