package site.siredvin.broccolium.modules.storage.fluid

import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import site.siredvin.broccolium.modules.storage.base.api.SomethingOperator
import site.siredvin.broccolium.modules.storage.fluid.api.AgnosticFluidStorage
import java.util.function.Predicate

class ForgeAgnosticFluidStorage(private val handler: IFluidHandler) : AgnosticFluidStorage {
    override fun getContent(): Iterator<AgnosticFluidStack> = (0 until handler.tanks).map {
        handler.getFluidInTank(it).toVanilla()
    }.iterator()

    override fun getCapacities(): List<Double> = (0 until handler.tanks).map { handler.getTankCapacity(it).toDouble() }

    override fun take(predicate: Predicate<AgnosticFluidStack>, limit: Double, simulate: Boolean): AgnosticFluidStack {
        var realLimit = limit
        var forgeStack = FluidStack.EMPTY
        val action = if (simulate) {
            IFluidHandler.FluidAction.SIMULATE
        } else {
            IFluidHandler.FluidAction.EXECUTE
        }
        for (i in 0 until handler.tanks) {
            val storedFluid = handler.getFluidInTank(i)
            if (predicate.test(storedFluid.toVanilla()) && (forgeStack.isEmpty || FluidStack.isSameFluidSameComponents(storedFluid, forgeStack))) {
                val extractedStack = handler.drain(storedFluid.copyWithCount(minOf(storedFluid.amount.toDouble(), realLimit).toInt()), action)
                if (!extractedStack.isEmpty) {
                    if (!forgeStack.isEmpty) {
                        forgeStack.amount += extractedStack.amount
                    } else {
                        forgeStack = extractedStack
                    }
                    realLimit -= extractedStack.amount
                    if (realLimit <= 0) {
                        return forgeStack.toVanilla()
                    }
                }
            }
        }
        return forgeStack.toVanilla()
    }

    override fun store(stack: AgnosticFluidStack, simulate: Boolean): AgnosticFluidStack {
        val forgeStack = stack.toForge()
        val action = if (simulate) {
            IFluidHandler.FluidAction.SIMULATE
        } else {
            IFluidHandler.FluidAction.EXECUTE
        }
        val filled = handler.fill(forgeStack, action)
        forgeStack.shrink(filled)
        if (forgeStack.isEmpty) {
            return AgnosticFluidStack.EMPTY
        }
        return forgeStack.toVanilla()
    }

    override fun setChanged() {
    }

    override val maxStackSize: Double
        get() = Long.MAX_VALUE.toDouble()
    override val operator: SomethingOperator<AgnosticFluidStack, Double>
        get() = FluidStorageUtils
}
