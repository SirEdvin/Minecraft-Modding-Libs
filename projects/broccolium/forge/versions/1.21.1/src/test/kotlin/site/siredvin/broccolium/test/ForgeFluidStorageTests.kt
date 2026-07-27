package site.siredvin.broccolium.test

import net.neoforged.neoforge.fluids.IFluidTank
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import net.neoforged.neoforge.fluids.capability.templates.FluidTank
import site.siredvin.broccolium.modules.storage.fluid.AgnosticFluidStack
import site.siredvin.broccolium.modules.storage.fluid.ForgeAgnosticFluidStorage
import site.siredvin.broccolium.modules.storage.fluid.api.AgnosticFluidStorage
import site.siredvin.broccolium.modules.storage.fluid.copyWithCount
import site.siredvin.broccolium.modules.storage.fluid.toForge
import site.siredvin.broccolium.test.storage.DummyFluidStorage

internal class CombinedTanks(private val tanks: List<IFluidTank>) : IFluidHandler {
    override fun getTanks(): Int = tanks.size

    override fun getFluidInTank(tank: Int): net.neoforged.neoforge.fluids.FluidStack = tanks[tank].fluid

    override fun getTankCapacity(tank: Int): Int = tanks[tank].capacity

    override fun isFluidValid(tank: Int, stack: net.neoforged.neoforge.fluids.FluidStack): Boolean = tanks[tank].isFluidValid(stack)

    override fun fill(resource: net.neoforged.neoforge.fluids.FluidStack, action: IFluidHandler.FluidAction): Int {
        val slidingStack = resource.copy()
        var filledAmount = 0
        tanks.forEach {
            if (it.isFluidValid(slidingStack)) {
                val partFilledAmount = it.fill(slidingStack, action)
                filledAmount += partFilledAmount
                slidingStack.shrink(filledAmount)
            }
        }
        return filledAmount
    }

    override fun drain(
        resource: net.neoforged.neoforge.fluids.FluidStack,
        action: IFluidHandler.FluidAction,
    ): net.neoforged.neoforge.fluids.FluidStack {
        val collectedStack = resource.copyWithCount(0)
        val slidingDrain = resource.copy()
        tanks.forEach {
            val drainedStack = it.drain(slidingDrain, action)
            collectedStack.grow(drainedStack.amount)
            slidingDrain.shrink(drainedStack.amount)
        }
        return collectedStack
    }

    override fun drain(maxDrain: Int, action: IFluidHandler.FluidAction): net.neoforged.neoforge.fluids.FluidStack {
        var slidingMaxDrain = maxDrain
        var currentStack = net.neoforged.neoforge.fluids.FluidStack.EMPTY
        tanks.forEach {
            if (currentStack.isEmpty) {
                val drainedStack = it.drain(slidingMaxDrain, action)
                if (!drainedStack.isEmpty) {
                    currentStack = drainedStack
                    slidingMaxDrain -= drainedStack.amount
                }
            } else {
                val drainedStack = it.drain(currentStack.copyWithCount(slidingMaxDrain), action)
                currentStack.grow(drainedStack.amount)
                slidingMaxDrain -= drainedStack.amount
            }
        }
        return currentStack
    }
}

@WithMinecraft
internal class ForgeFluidStorageTests : FluidStorageTests() {

    override fun createStorage(fluids: List<AgnosticFluidStack>, secondary: Boolean): AgnosticFluidStorage {
        return ForgeAgnosticFluidStorage(
            CombinedTanks(
                fluids.map { stack ->
                    val storage = FluidTank(1000)
                    if (!stack.isEmpty) {
                        storage.fill(stack.toForge(), IFluidHandler.FluidAction.EXECUTE)
                    }
                    return@map storage
                },
            ),
        )
    }
}

@WithMinecraft
internal class ForgeDummyFluidStorageTests : FluidStorageTests() {

    override fun createStorage(fluids: List<AgnosticFluidStack>, secondary: Boolean): AgnosticFluidStorage {
        if (secondary) {
            return DummyFluidStorage(fluids.size, fluids)
        }
        return ForgeAgnosticFluidStorage(
            CombinedTanks(
                fluids.map { stack ->
                    val storage = FluidTank(1000)
                    if (!stack.isEmpty) {
                        storage.fill(stack.toForge(), IFluidHandler.FluidAction.EXECUTE)
                    }
                    return@map storage
                },
            ),
        )
    }
}
