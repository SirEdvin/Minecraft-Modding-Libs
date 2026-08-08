package site.siredvin.broccolium.modules.storage

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.neoforged.neoforge.capabilities.Capabilities
import site.siredvin.broccolium.modules.storage.base.api.AgnosticStorage
import site.siredvin.broccolium.modules.storage.base.api.SlottedAgnosticStorage
import site.siredvin.broccolium.modules.storage.energy.AgnosticEnergyHandlerWrapper
import site.siredvin.broccolium.modules.storage.energy.api.AgnosticEnergyStorage
import site.siredvin.broccolium.modules.storage.fluid.ForgeAgnosticFluidStorage
import site.siredvin.broccolium.modules.storage.fluid.api.AgnosticFluidStorage
import site.siredvin.broccolium.modules.storage.item.AgnosticItemHandlerWrapper

object ForgeStorageUtils {

    @Suppress("UNUSED_PARAMETER")
    fun extractStorageFromBlock(level: Level, pos: BlockPos, blockEntity: BlockEntity?, direction: Direction?): SlottedAgnosticStorage<ItemStack, Int>? {
        if (blockEntity == null) {
            return null
        }
        if (blockEntity.isRemoved) return null
        val itemHandler = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, direction) ?: return null
        return AgnosticItemHandlerWrapper(itemHandler)
    }

    @Suppress("UNUSED_PARAMETER")
    fun extractStorageFromEntity(level: Level, entity: Entity, direction: Direction?): AgnosticStorage<ItemStack, Int>? {
        val itemHandler = entity.getCapability(Capabilities.ItemHandler.ENTITY, null as Void) ?: return null
        return AgnosticItemHandlerWrapper(itemHandler)
    }

    @Suppress("UNUSED_PARAMETER")
    fun extractItemStorageFromItem(level: Level, origin: SlottedAgnosticStorage<ItemStack, Int>, slot: Int): AgnosticStorage<ItemStack, Int>? {
        val storage = origin.get(slot).getCapability(Capabilities.ItemHandler.ITEM) ?: return null
        return AgnosticItemHandlerWrapper(storage)
    }

    @Suppress("UNUSED_PARAMETER")
    fun extractFluidStorageFromBlock(level: Level, pos: BlockPos, blockEntity: BlockEntity?, direction: Direction?): AgnosticFluidStorage? {
        if (blockEntity == null) return null
        if (blockEntity.isRemoved) return null
        val fluidHandler = level.getCapability(Capabilities.FluidHandler.BLOCK, pos, direction) ?: return null
        return ForgeAgnosticFluidStorage(fluidHandler)
    }

    @Suppress("UNUSED_PARAMETER")
    fun extractFluidStorageFromEntity(level: Level, entity: Entity, direction: Direction?): AgnosticFluidStorage? {
        val fluidHandler = entity.getCapability(Capabilities.FluidHandler.ENTITY, direction) ?: return null
        return ForgeAgnosticFluidStorage(fluidHandler)
    }

    @Suppress("UNUSED_PARAMETER")
    fun extractFluidStorageFromItem(level: Level, origin: SlottedAgnosticStorage<ItemStack, Int>, slot: Int): AgnosticFluidStorage? {
        val storage = origin.get(slot).getCapability(Capabilities.FluidHandler.ITEM) ?: return null
        return ForgeAgnosticFluidStorage(storage)
    }

    @Suppress("UNUSED_PARAMETER")
    fun extractEnergyStorageFromBlock(level: Level, pos: BlockPos, blockEntity: BlockEntity?, direction: Direction?): AgnosticEnergyStorage? {
        if (blockEntity == null) return null
        if (blockEntity.isRemoved) return null
        val energyStorage = level.getCapability(Capabilities.EnergyStorage.BLOCK, pos, direction) ?: return null
        return AgnosticEnergyHandlerWrapper(energyStorage)
    }

    @Suppress("UNUSED_PARAMETER")
    fun extractEnergyStorageFromItem(level: Level, origin: SlottedAgnosticStorage<ItemStack, Int>, slot: Int): AgnosticEnergyStorage? {
        val energyStorage = origin.get(slot).getCapability(Capabilities.EnergyStorage.ITEM) ?: return null
        return AgnosticEnergyHandlerWrapper(energyStorage)
    }

    @Suppress("UNUSED_PARAMETER")
    fun extractEnergyStorageFromEntity(level: Level, entity: Entity, direction: Direction?): AgnosticEnergyStorage? {
        val energyStorage = entity.getCapability(Capabilities.EnergyStorage.ENTITY, direction) ?: return null
        return AgnosticEnergyHandlerWrapper(energyStorage)
    }
}
