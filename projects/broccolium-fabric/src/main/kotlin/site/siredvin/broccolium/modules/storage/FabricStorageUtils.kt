package site.siredvin.broccolium.modules.storage

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import site.siredvin.broccolium.BroccoliumCore
import site.siredvin.broccolium.modules.platform.PlatformToolkit
import site.siredvin.broccolium.modules.storage.base.api.AgnosticSink
import site.siredvin.broccolium.modules.storage.base.api.AgnosticStorage
import site.siredvin.broccolium.modules.storage.base.api.SlottedAgnosticSink
import site.siredvin.broccolium.modules.storage.base.api.SlottedAgnosticStorage
import site.siredvin.broccolium.modules.storage.fluid.AgnosticFluidStack
import site.siredvin.broccolium.modules.storage.fluid.FabricAgnosticFluidStorage
import site.siredvin.broccolium.modules.storage.fluid.api.AgnosticFluidStorage
import site.siredvin.broccolium.modules.storage.fluid.toVanilla
import site.siredvin.broccolium.modules.storage.fluid.toVariant
import site.siredvin.broccolium.modules.storage.item.FabricSlottedStorageWrapper
import site.siredvin.broccolium.modules.storage.item.FabricStorageWrapper
import site.siredvin.broccolium.modules.storage.item.SlottedAgnosticItemStorageWrapper
import java.util.function.Predicate
import net.fabricmc.fabric.api.transfer.v1.storage.Storage as FabricStorage

object FabricStorageUtils {

    const val MOVABLE_TYPE = "fabricTransaction"

    private class PredicateWrapper(private val predicate: Predicate<ItemStack>) : Predicate<ItemVariant> {
        override fun test(p0: ItemVariant): Boolean = predicate.test(p0.toStack())
    }

    fun wrapItem(predicate: Predicate<ItemStack>): Predicate<ItemVariant> = PredicateWrapper(predicate)

    fun wrapFluid(predicate: Predicate<AgnosticFluidStack>): Predicate<FluidVariant> = Predicate<FluidVariant> { predicate.test(it.toVanilla()) }

    /**
     * Generic move to any targetable storage, should be used only after make sure that to is not fabric one related!
     */
    fun moveToTargetable(storage: FabricStorage<ItemVariant>, to: AgnosticSink<ItemStack, Int>, limit: Int, toSlot: Int, takePredicate: Predicate<ItemStack>): Int {
        assert(to.movableType != MOVABLE_TYPE)

        val transaction = Transaction.openOuter()
        transaction.use {
            val resource =
                StorageUtil.findExtractableResource(storage, wrapItem(takePredicate), it)
                    ?: return 0
            val extractedAmount = storage.extract(resource, limit.toLong(), it).toInt()
            if (extractedAmount == 0) {
                return 0
            }
            val insertionStack = resource.toStack(extractedAmount)
            val remainder = if (toSlot < 0) {
                to.store(insertionStack, false)
            } else {
                (to as SlottedAgnosticSink<ItemStack, Int>).store(insertionStack, toSlot, toSlot, false)
            }
            val insertedCount = extractedAmount - remainder.count
            if (!remainder.isEmpty) {
                storage.insert(resource, remainder.count.toLong(), it)
            }
            it.commit()
            return insertedCount
        }
    }

    /**
     * Generic move from any targetable storage, should be used only after make sure that to is not fabric one related!
     */
    fun moveFromTargetable(from: AgnosticStorage<ItemStack, Int>, to: FabricStorage<ItemVariant>, limit: Int, fromSlot: Int, takePredicate: Predicate<ItemStack>): Int {
        assert(from.movableType != MOVABLE_TYPE)

        val insertionStack = if (fromSlot < 0) {
            from.take(takePredicate, limit, false)
        } else {
            if (from !is SlottedAgnosticStorage<ItemStack, Int>) {
                BroccoliumCore.LOGGER.warn("From doesn't support slotting, so slot is just ignore")
                from.take(takePredicate, limit, false)
            } else {
                from.take(limit, fromSlot, fromSlot, takePredicate, false)
            }
        }
        if (insertionStack.isEmpty) {
            return 0
        }

        val transaction = Transaction.openOuter()
        transaction.use {
            val insertedAmount = to.insert(ItemVariant.of(insertionStack), insertionStack.count.toLong(), it)

            val remainCount = insertionStack.count - insertedAmount
            if (remainCount > 0) {
                if (fromSlot > -1 && from is SlottedAgnosticStorage<ItemStack, Int>) {
                    from.store(insertionStack.copyWithCount(remainCount.toInt()), fromSlot, fromSlot, false)
                } else {
                    from.store(insertionStack.copyWithCount(remainCount.toInt()), false)
                }
            }
            it.commit()
            return insertedAmount.toInt()
        }
    }

    fun moveToTargetable(storage: FabricStorage<FluidVariant>, to: AgnosticSink<AgnosticFluidStack, Double>, limit: Double, takePredicate: Predicate<AgnosticFluidStack>): Double {
        assert(to.movableType != MOVABLE_TYPE)

        val platformLimit = (limit * PlatformToolkit.get().fluidCompactDivider).toLong()

        val transaction = Transaction.openOuter()
        transaction.use {
            val resource =
                StorageUtil.findExtractableResource(storage, wrapFluid(takePredicate), it)
                    ?: return 0.0
            val extractedAmount = storage.extract(resource, platformLimit, it)
            if (extractedAmount == 0L) {
                return 0.0
            }
            val insertionStack = resource.toVanilla(extractedAmount.toDouble())
            val remainder = to.store(insertionStack, false)
            val insertedCount = extractedAmount - remainder.platformAmount
            if (!remainder.isEmpty) {
                storage.insert(resource, remainder.platformAmount.toLong(), it)
            }
            it.commit()
            return insertedCount / PlatformToolkit.get().fluidCompactDivider
        }
    }

    fun moveFromTargetable(from: AgnosticStorage<AgnosticFluidStack, Double>, to: FabricStorage<FluidVariant>, limit: Double, takePredicate: Predicate<AgnosticFluidStack>): Double {
        assert(from.movableType != MOVABLE_TYPE)

        val platformLimit = limit * PlatformToolkit.get().fluidCompactDivider

        val insertionStack = from.take(takePredicate, platformLimit, false)
        if (insertionStack.isEmpty) {
            return 0.0
        }

        val transaction = Transaction.openOuter()
        transaction.use {
            val insertedAmount = to.insert(insertionStack.toVariant(), insertionStack.platformAmount.toLong(), it)

            val remainCount = insertionStack.platformAmount - insertedAmount
            if (remainCount > 0) {
                from.store(insertionStack.copyWithCount(remainCount / PlatformToolkit.get().fluidCompactDivider), false)
            }
            it.commit()
            return insertedAmount / PlatformToolkit.get().fluidCompactDivider.toDouble()
        }
    }

    fun getSlot(storage: SlottedAgnosticStorage<ItemStack, Int>, slot: Int): SingleSlotStorage<ItemVariant> {
        if (storage is FabricSlottedStorageWrapper) {
            return storage.storage.getSlot(slot)
        }
        return SlottedAgnosticItemStorageWrapper.of(storage).getSlot(slot)
    }

    fun extractStorage(level: Level, pos: BlockPos, @Suppress("UNUSED_PARAMETER") blockEntity: BlockEntity?, direction: Direction?): AgnosticStorage<ItemStack, Int>? {
        var itemStorage = ItemStorage.SIDED.find(level, pos, null)
        if (itemStorage == null) {
            if (direction != null) {
                itemStorage = ItemStorage.SIDED.find(level, pos, direction) ?: return null
            } else {
                return null
            }
        }

        return if (itemStorage is net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage) {
            FabricSlottedStorageWrapper(itemStorage)
        } else {
            FabricStorageWrapper(itemStorage)
        }
    }

    fun extractFluidStorage(level: Level, pos: BlockPos, @Suppress("UNUSED_PARAMETER") blockEntity: BlockEntity?, direction: Direction?): AgnosticFluidStorage? {
        var fluidStorage = FluidStorage.SIDED.find(level, pos, null)
        if (fluidStorage == null) {
            if (direction != null) {
                fluidStorage = FluidStorage.SIDED.find(level, pos, direction) ?: return null
            } else {
                return null
            }
        }
        return FabricAgnosticFluidStorage(fluidStorage)
    }

    fun extractFluidStorageFromItem(@Suppress("UNUSED_PARAMETER") level: Level, origin: SlottedAgnosticStorage<ItemStack, Int>, slot: Int): AgnosticFluidStorage? {
        val fluidStorage = FluidStorage.ITEM.find(origin.get(slot), ContainerItemContext.ofSingleSlot(getSlot(origin, slot))) ?: return null
        return FabricAgnosticFluidStorage(fluidStorage)
    }
}
