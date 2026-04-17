package site.siredvin.broccolium.modules.storage

import net.minecraft.core.BlockPos
import net.minecraft.world.Container
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.capabilities.ICapabilityProvider
import net.neoforged.neoforge.energy.IEnergyStorage
import site.siredvin.broccolium.modules.storage.energy.AgnosticEnergyHandlerWrapper
import site.siredvin.broccolium.modules.storage.energy.api.AgnosticEnergyStorage
import site.siredvin.broccolium.modules.storage.fluid.ForgeAgnosticFluidStorage
import site.siredvin.broccolium.modules.storage.fluid.api.AgnosticFluidStorage
import site.siredvin.broccolium.modules.storage.item.AgnosticItemHandlerWrapper
import site.siredvin.broccolium.modules.storage.item.api.AgnosticItemStorage
import site.siredvin.broccolium.modules.storage.item.api.SlottedAgnosticItemStorage

object ForgeStorageUtils {

    @Suppress("UNUSED_PARAMETER")
    fun extractStorageFromBlock(level: Level, pos: BlockPos, blockEntity: BlockEntity?): SlottedAgnosticItemStorage? {
        val itemHandler = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, null) ?: return null
        return AgnosticItemHandlerWrapper(itemHandler)
    }

    @Suppress("UNUSED_PARAMETER")
    fun extractStorageFromEntity(level: Level, entity: Entity): AgnosticItemStorage? {
        val itemHandler = entity.getCapability(Capabilities.ItemHandler.ENTITY, null) ?: return null
        return AgnosticItemHandlerWrapper(itemHandler)
    }

    @Suppress("UNUSED_PARAMETER")
    fun extractFluidStorageFromBlock(level: Level, pos: BlockPos, blockEntity: BlockEntity?): AgnosticFluidStorage? {
        val fluidHandler = level.getCapability(Capabilities.FluidHandler.BLOCK, pos, null) ?: return null
        return ForgeAgnosticFluidStorage(fluidHandler)
    }

    @Suppress("UNUSED_PARAMETER")
    fun extractFluidStorageFromEntity(level: Level, entity: Entity): AgnosticFluidStorage? {
        val fluidHandler = entity.getCapability(Capabilities.FluidHandler.ENTITY, null) ?: return null
        return ForgeAgnosticFluidStorage(fluidHandler)
    }

    @Suppress("UNUSED_PARAMETER")
    fun extractEnergyStorageFromBlock(level: Level, pos: BlockPos, blockEntity: BlockEntity?): AgnosticEnergyStorage? {
        val energyStorage = level.getCapability(Capabilities.EnergyStorage.BLOCK, pos, null) ?: return null
        return AgnosticEnergyHandlerWrapper(energyStorage)
    }

    @Suppress("UNUSED_PARAMETER")
    fun extractEnergyStorageFromItem(level: Level, stack: ItemStack): AgnosticEnergyStorage? {
        val energyStorage = stack.getCapability(Capabilities.EnergyStorage.ITEM) ?: return null
        return AgnosticEnergyHandlerWrapper(energyStorage)
    }
}
