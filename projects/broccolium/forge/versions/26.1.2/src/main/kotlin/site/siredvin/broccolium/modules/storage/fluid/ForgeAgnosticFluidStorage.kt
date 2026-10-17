package site.siredvin.broccolium.modules.storage.fluid

import net.neoforged.neoforge.transfer.ResourceHandler
import net.neoforged.neoforge.transfer.fluid.FluidResource
import net.neoforged.neoforge.transfer.transaction.Transaction
import site.siredvin.broccolium.modules.storage.base.api.SomethingOperator
import site.siredvin.broccolium.modules.storage.fluid.api.AgnosticFluidStorage
import java.util.function.Predicate

class ForgeAgnosticFluidStorage(private val handler: ResourceHandler<FluidResource>) : AgnosticFluidStorage {
    override fun getContent(): Iterator<AgnosticFluidStack> = (0 until handler.size()).map { index ->
        val resource = handler.getResource(index)
        if (resource.isEmpty) AgnosticFluidStack.EMPTY else AgnosticFluidStack(resource.fluid, handler.getAmountAsLong(index).toDouble(), resource.componentsPatch)
    }.iterator()

    override fun getCapacities(): List<Double> = (0 until handler.size()).map { handler.getCapacityAsLong(it, FluidResource.EMPTY).toDouble() }

    override fun take(predicate: Predicate<AgnosticFluidStack>, limit: Double, simulate: Boolean): AgnosticFluidStack {
        require(limit.isFinite() && limit >= 0) { "Invalid fluid limit" }
        val bounded = limit.coerceAtMost(Int.MAX_VALUE.toDouble()).toInt()
        if (bounded == 0) return AgnosticFluidStack.EMPTY
        Transaction.openRoot().use { transaction ->
            var selected: FluidResource? = null
            var extracted = 0
            for (index in 0 until handler.size()) {
                val resource = handler.getResource(index)
                if (resource.isEmpty || (selected != null && resource != selected)) continue
                if (!predicate.test(AgnosticFluidStack(resource.fluid, handler.getAmountAsLong(index).toDouble(), resource.componentsPatch))) continue
                val amount = handler.extract(index, resource, bounded - extracted, transaction)
                if (amount > 0) {
                    selected = resource
                    extracted += amount
                }
                if (extracted == bounded) break
            }
            if (!simulate) transaction.commit()
            return selected?.let { AgnosticFluidStack(it.fluid, extracted.toDouble(), it.componentsPatch) } ?: AgnosticFluidStack.EMPTY
        }
    }

    override fun store(stack: AgnosticFluidStack, simulate: Boolean): AgnosticFluidStack {
        require(stack.amount.isFinite() && stack.amount >= 0) { "Invalid fluid amount" }
        if (stack.isEmpty) return stack.copy()
        val resource = FluidResource.of(stack.fluid, stack.components)
        val bounded = stack.amount.coerceAtMost(Int.MAX_VALUE.toDouble()).toInt()
        if (bounded == 0) return stack.copy()
        Transaction.openRoot().use { transaction ->
            val inserted = handler.insert(resource, bounded, transaction)
            if (!simulate) transaction.commit()
            return stack.copyWithCount(stack.amount - inserted)
        }
    }

    override fun setChanged() {
    }

    override val maxStackSize: Double
        get() = Long.MAX_VALUE.toDouble()
    override val operator: SomethingOperator<AgnosticFluidStack, Double>
        get() = FluidStorageUtils
}
