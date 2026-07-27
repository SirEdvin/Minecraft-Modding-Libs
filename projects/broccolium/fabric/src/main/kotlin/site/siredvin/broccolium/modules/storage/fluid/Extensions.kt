package site.siredvin.broccolium.modules.storage.fluid

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView
import site.siredvin.broccolium.modules.platform.PlatformToolkit

fun StorageView<FluidVariant>.toVanilla(): AgnosticFluidStack = this.resource.toVanilla(this.amount.toDouble())

fun FluidVariant.toVanilla(count: Double = 1.0): AgnosticFluidStack = AgnosticFluidStack(this.fluid, count / PlatformToolkit.get().fluidCompactDivider, this.nbt)

fun AgnosticFluidStack.toVariant(): FluidVariant = FluidVariant.of(this.fluid, this.tag)
