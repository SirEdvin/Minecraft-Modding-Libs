package site.siredvin.broccolium.modules.storage.fluid

import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import site.siredvin.broccolium.modules.storage.fluid.api.AgnosticFluidStorage
import java.util.function.Predicate

class ForgeAgnosticFluidStorage(private val handler: IFluidHandler) : AgnosticFluidStorage {
    override fun getFluids(): Iterator<AgnosticFluidStack> = (0 until handler.tanks).map {
        handler.getFluidInTank(it).toVanilla()
    }.iterator()

    override fun getCapacities(): List<Double> = (0 until handler.tanks).map { handler.getTankCapacity(it).toDouble() }

    override fun takeFluid(predicate: Predicate<AgnosticFluidStack>, limit: Double): AgnosticFluidStack {
        var realLimit = limit
        var forgeStack = FluidStack.EMPTY
        for (i in 0 until handler.tanks) {
            val storedFluid = handler.getFluidInTank(i)
            if (predicate.test(storedFluid.toVanilla()) && (forgeStack.isEmpty || FluidStack.isSameFluidSameComponents(storedFluid, forgeStack))) {
                val extractedStack: FluidStack = handler.drain(storedFluid.copyWithCount(minOf(storedFluid.amount.toDouble(), realLimit).toInt()), IFluidHandler.FluidAction.EXECUTE)
                if (!extractedStack.isEmpty()) {
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

    override fun storeFluid(stack: AgnosticFluidStack): AgnosticFluidStack {
        val forgeStack = stack.toForge()
        for (i in 0 until handler.tanks) {
            if (handler.isFluidValid(i, forgeStack)) {
                val filled = handler.fill(forgeStack, IFluidHandler.FluidAction.EXECUTE)
                forgeStack.shrink(filled)
                if (forgeStack.isEmpty) {
                    return AgnosticFluidStack.EMPTY
                }
            }
        }
        return forgeStack.toVanilla()
    }

    override fun setChanged() {
    }
}
