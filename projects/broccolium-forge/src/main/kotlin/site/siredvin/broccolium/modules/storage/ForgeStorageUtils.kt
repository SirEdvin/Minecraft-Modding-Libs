package site.siredvin.broccolium.modules.storage

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.Container
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.energy.IEnergyStorage
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.wrapper.InvWrapper
import site.siredvin.broccolium.modules.storage.energy.AgnosticEnergyHandlerWrapper
import site.siredvin.broccolium.modules.storage.energy.api.AgnosticEnergyStorage
import site.siredvin.broccolium.modules.storage.fluid.ForgeAgnosticFluidStorage
import site.siredvin.broccolium.modules.storage.fluid.api.AgnosticFluidStorage
import site.siredvin.broccolium.modules.storage.item.AgnosticItemHandlerWrapper
import site.siredvin.broccolium.modules.storage.item.api.AgnosticItemStorage
import site.siredvin.broccolium.modules.storage.item.api.SlottedAgnosticItemStorage

object ForgeStorageUtils {
    private fun extractEnergyStorage(something: Any?, direction: Direction?): IEnergyStorage? {
        if (something is BlockEntity && something.isRemoved) return null
        if (something is ICapabilityProvider) {
            val cap: LazyOptional<IEnergyStorage> = something.getCapability(ForgeCapabilities.ENERGY)
            if (cap.isPresent) return cap.orElseThrow { NullPointerException() }
            if (direction != null) {
                val cap: LazyOptional<IEnergyStorage> = something.getCapability(ForgeCapabilities.ENERGY, direction)
                if (cap.isPresent) return cap.orElseThrow { NullPointerException() }
            }
        }
        return something as? IEnergyStorage
    }
    private fun extractFluidHandler(something: Any?, direction: Direction?): IFluidHandler? {
        if (something is BlockEntity && something.isRemoved) return null
        if (something is ICapabilityProvider) {
            val cap: LazyOptional<IFluidHandler> = something.getCapability(ForgeCapabilities.FLUID_HANDLER)
            if (cap.isPresent) return cap.orElseThrow { NullPointerException() }
            if (direction != null) {
                val cap: LazyOptional<IFluidHandler> = something.getCapability(ForgeCapabilities.FLUID_HANDLER, direction)
                if (cap.isPresent) return cap.orElseThrow { NullPointerException() }
            }
        }
        return something as? IFluidHandler
    }

    private fun extractItemHandler(something: Any?, direction: Direction?): IItemHandler? {
        if (something is BlockEntity && something.isRemoved) return null
        if (something is ICapabilityProvider) {
            val cap: LazyOptional<IItemHandler> = something.getCapability(ForgeCapabilities.ITEM_HANDLER)
            if (cap.isPresent) return cap.orElseThrow { NullPointerException() }
            if (direction != null) {
                val cap: LazyOptional<IItemHandler> = something.getCapability(ForgeCapabilities.ITEM_HANDLER, direction)
                if (cap.isPresent) return cap.orElseThrow { NullPointerException() }
            }
        }
        return something as? IItemHandler ?: (something as? Container)?.let { InvWrapper(it) }
    }

    @Suppress("UNUSED_PARAMETER")
    fun extractStorageFromBlock(level: Level, pos: BlockPos, blockEntity: BlockEntity?, direction: Direction?): SlottedAgnosticItemStorage? {
        if (blockEntity == null) {
            return null
        }
        val itemHandler = extractItemHandler(blockEntity, direction) ?: return null
        return AgnosticItemHandlerWrapper(itemHandler)
    }

    @Suppress("UNUSED_PARAMETER")
    fun extractStorageFromEntity(level: Level, entity: Entity, direction: Direction?): AgnosticItemStorage? {
        val itemHandler = entity as? IItemHandler ?: return null
        return AgnosticItemHandlerWrapper(itemHandler)
    }

    @Suppress("UNUSED_PARAMETER")
    fun extractItemStorageFromItem(level: Level, origin: SlottedAgnosticItemStorage, slot: Int): AgnosticItemStorage? {
        val storage = extractItemHandler(origin.getItem(slot), null) ?: return null
        return AgnosticItemHandlerWrapper(storage)
    }

    @Suppress("UNUSED_PARAMETER")
    fun extractFluidStorageFromBlock(level: Level, pos: BlockPos, blockEntity: BlockEntity?, direction: Direction?): AgnosticFluidStorage? {
        if (blockEntity == null) return null
        val fluidHandler = extractFluidHandler(blockEntity, direction) ?: return null
        return ForgeAgnosticFluidStorage(fluidHandler)
    }

    @Suppress("UNUSED_PARAMETER")
    fun extractFluidStorageFromEntity(level: Level, entity: Entity, direction: Direction?): AgnosticFluidStorage? {
        val fluidHandler = entity as? IFluidHandler ?: return null
        return ForgeAgnosticFluidStorage(fluidHandler)
    }

    @Suppress("UNUSED_PARAMETER")
    fun extractFluidStorageFromItem(level: Level, origin: SlottedAgnosticItemStorage, slot: Int): AgnosticFluidStorage? {
        val storage = extractFluidHandler(origin.getItem(slot), null) ?: return null
        return ForgeAgnosticFluidStorage(storage)
    }

    @Suppress("UNUSED_PARAMETER")
    fun extractEnergyStorageFromBlock(level: Level, pos: BlockPos, blockEntity: BlockEntity?, direction: Direction?): AgnosticEnergyStorage? {
        if (blockEntity == null) return null
        val energyStorage = extractEnergyStorage(blockEntity, direction) ?: return null
        return AgnosticEnergyHandlerWrapper(energyStorage)
    }

    @Suppress("UNUSED_PARAMETER")
    fun extractEnergyStorageFromItem(level: Level, origin: SlottedAgnosticItemStorage, slot: Int): AgnosticEnergyStorage? {
        val energyStorage = extractEnergyStorage(origin.getItem(slot), null) ?: return null
        return AgnosticEnergyHandlerWrapper(energyStorage)
    }

    @Suppress("UNUSED_PARAMETER")
    fun extractEnergyStorageFromEntity(level: Level, entity: Entity, direction: Direction?): AgnosticEnergyStorage? {
        val energyStorage = entity as? IEnergyStorage ?: return null
        return AgnosticEnergyHandlerWrapper(energyStorage)
    }
}
