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
import site.siredvin.broccolium.modules.storage.item.AgnosticItemAccess
import site.siredvin.broccolium.modules.storage.item.AgnosticItemHandlerWrapper

object ForgeStorageUtils {
    fun extractStorageFromBlock(level: Level, pos: BlockPos, blockEntity: BlockEntity?, direction: Direction?): SlottedAgnosticStorage<ItemStack, Int>? {
        if (blockEntity == null || blockEntity.isRemoved) return null
        val handler = level.getCapability(Capabilities.Item.BLOCK, pos, direction) ?: return null
        return AgnosticItemHandlerWrapper(handler)
    }

    @Suppress("UNUSED_PARAMETER")
    fun extractStorageFromEntity(level: Level, entity: Entity, direction: Direction?): AgnosticStorage<ItemStack, Int>? {
        val handler = entity.getCapability(Capabilities.Item.ENTITY, null) ?: return null
        return AgnosticItemHandlerWrapper(handler)
    }

    @Suppress("UNUSED_PARAMETER")
    fun extractItemStorageFromItem(level: Level, origin: SlottedAgnosticStorage<ItemStack, Int>, slot: Int): AgnosticStorage<ItemStack, Int>? {
        val handler = AgnosticItemAccess.of(origin, slot).getCapability(Capabilities.Item.ITEM) ?: return null
        return AgnosticItemHandlerWrapper(handler)
    }

    fun extractFluidStorageFromBlock(level: Level, pos: BlockPos, blockEntity: BlockEntity?, direction: Direction?): AgnosticFluidStorage? {
        if (blockEntity == null || blockEntity.isRemoved) return null
        val handler = level.getCapability(Capabilities.Fluid.BLOCK, pos, direction) ?: return null
        return ForgeAgnosticFluidStorage(handler)
    }

    @Suppress("UNUSED_PARAMETER")
    fun extractFluidStorageFromEntity(level: Level, entity: Entity, direction: Direction?): AgnosticFluidStorage? {
        val handler = entity.getCapability(Capabilities.Fluid.ENTITY, direction) ?: return null
        return ForgeAgnosticFluidStorage(handler)
    }

    @Suppress("UNUSED_PARAMETER")
    fun extractFluidStorageFromItem(level: Level, origin: SlottedAgnosticStorage<ItemStack, Int>, slot: Int): AgnosticFluidStorage? {
        val handler = AgnosticItemAccess.of(origin, slot).getCapability(Capabilities.Fluid.ITEM) ?: return null
        return ForgeAgnosticFluidStorage(handler)
    }

    fun extractEnergyStorageFromBlock(level: Level, pos: BlockPos, blockEntity: BlockEntity?, direction: Direction?): AgnosticEnergyStorage? {
        if (blockEntity == null || blockEntity.isRemoved) return null
        val handler = level.getCapability(Capabilities.Energy.BLOCK, pos, direction) ?: return null
        return AgnosticEnergyHandlerWrapper(handler)
    }

    @Suppress("UNUSED_PARAMETER")
    fun extractEnergyStorageFromItem(level: Level, origin: SlottedAgnosticStorage<ItemStack, Int>, slot: Int): AgnosticEnergyStorage? {
        val handler = AgnosticItemAccess.of(origin, slot).getCapability(Capabilities.Energy.ITEM) ?: return null
        return AgnosticEnergyHandlerWrapper(handler)
    }

    @Suppress("UNUSED_PARAMETER")
    fun extractEnergyStorageFromEntity(level: Level, entity: Entity, direction: Direction?): AgnosticEnergyStorage? {
        val handler = entity.getCapability(Capabilities.Energy.ENTITY, direction) ?: return null
        return AgnosticEnergyHandlerWrapper(handler)
    }
}
