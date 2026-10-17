package site.siredvin.broccolium.modules.storage.fluid

import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.transfer.fluid.FluidResource

fun FluidStack.toVanilla(): AgnosticFluidStack {
    if (this.isEmpty) return AgnosticFluidStack.EMPTY
    return AgnosticFluidStack(this.fluid, this.amount.toDouble(), this.componentsPatch)
}

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
fun FluidStack.copyWithCount(count: Int): FluidStack = copy().also { it.amount = count }

fun AgnosticFluidStack.toForge(): FluidStack {
    if (this.isEmpty) return FluidStack.EMPTY
    return FluidResource.of(this.fluid, this.components).toStack(this.amount.toInt())
}
