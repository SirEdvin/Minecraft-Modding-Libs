package site.siredvin.broccolium.modules.storage.fluid

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction
import site.siredvin.broccolium.modules.platform.PlatformToolkit
import site.siredvin.broccolium.modules.storage.FabricStorageUtils
import site.siredvin.broccolium.modules.storage.base.api.AgnosticSink
import site.siredvin.broccolium.modules.storage.base.api.AgnosticStorage
import site.siredvin.broccolium.modules.storage.base.api.SomethingOperator
import site.siredvin.broccolium.modules.storage.fluid.api.AgnosticFluidStorage
import java.util.function.Predicate

open class FabricAgnosticFluidStorage(private val storage: Storage<FluidVariant>) : AgnosticFluidStorage {

    override val movableType: String
        get() = FabricStorageUtils.MOVABLE_TYPE

    override fun getContent(): Iterator<AgnosticFluidStack> = this.storage.map { it.toVanilla() }.iterator()
    override fun getCapacities(): List<Double> {
        val result = mutableListOf<Double>()
        this.storage.iterator().forEach {
            result.add(it.capacity.toDouble() / PlatformToolkit.get().fluidCompactDivider)
        }
        return result
    }

    override fun moveTo(
        to: AgnosticSink<AgnosticFluidStack, Double>,
        limit: Double,
        toSlot: Int,
        takePredicate: Predicate<AgnosticFluidStack>,
    ): Double {
        if (to.movableType == FabricStorageUtils.MOVABLE_TYPE) {
            return to.moveFrom(this, limit, toSlot, takePredicate)
        }
        if (to.movableType == null) {
            return FabricStorageUtils.moveToTargetable(this.storage, to, limit, takePredicate)
        }
        throw IllegalStateException("Cannot mix movable type, this should be impossible here")
    }

    override fun moveFrom(
        from: AgnosticStorage<AgnosticFluidStack, Double>,
        limit: Double,
        fromSlot: Int,
        takePredicate: Predicate<AgnosticFluidStack>,
    ): Double {
        if (from.movableType == FabricStorageUtils.MOVABLE_TYPE) {
            if (from !is FabricAgnosticFluidStorage) throw IllegalStateException("For fabricTransfer please use FabricFluidStorage")
            return StorageUtil.move(
                from.storage,
                storage,
                { takePredicate.test(it.toVanilla(1.0)) },
                (limit * PlatformToolkit.get().fluidCompactDivider).toLong(),
                null,
            ) / PlatformToolkit.get().fluidCompactDivider.toDouble()
        }
        if (from.movableType == null) {
            return FabricStorageUtils.moveFromTargetable(from, this.storage, limit, takePredicate)
        }
        throw IllegalStateException("Cannot mix movable type, this should be impossible here")
    }

    override fun take(predicate: Predicate<AgnosticFluidStack>, limit: Double, simulate: Boolean): AgnosticFluidStack {
        val platformLimit = limit * PlatformToolkit.get().fluidCompactDivider
        if (!storage.supportsExtraction()) return AgnosticFluidStack.EMPTY
        val extractableTarget = StorageUtil.findExtractableContent(storage, {
            predicate.test(it.toVanilla())
        }, null)
        if (extractableTarget == null || extractableTarget.amount == 0L) {
            return AgnosticFluidStack.EMPTY
        }
        val realLimit = minOf(maxStackSize, platformLimit)
        Transaction.openOuter().use {
            val extracted = storage.extract(extractableTarget.resource, realLimit.toLong(), it)
            if (!simulate) {
                it.commit()
            } else {
                it.abort()
            }
            return extractableTarget.resource.toVanilla(extracted.toDouble())
        }
    }

    override fun store(stack: AgnosticFluidStack, simulate: Boolean): AgnosticFluidStack {
        if (!storage.supportsInsertion()) return stack
        Transaction.openOuter().use {
            val inserted = storage.insert(stack.toVariant(), stack.platformAmount.toLong(), it)
            if (inserted == 0L) {
                it.abort()
                return stack
            }
            if (!simulate) {
                it.commit()
            } else {
                it.abort()
            }
            return stack.copyWithCount((stack.platformAmount - inserted) / PlatformToolkit.get().fluidCompactDivider)
        }
    }

    override fun setChanged() {
    }

    override val maxStackSize: Double
        get() = (Long.MAX_VALUE / PlatformToolkit.get().fluidCompactDivider).toDouble()
    override val operator: SomethingOperator<AgnosticFluidStack, Double>
        get() = FluidStorageUtils
}
