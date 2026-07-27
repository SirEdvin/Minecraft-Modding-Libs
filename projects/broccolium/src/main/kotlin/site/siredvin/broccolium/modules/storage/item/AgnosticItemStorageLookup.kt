package site.siredvin.broccolium.modules.storage.item

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.Container
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.vehicle.AbstractMinecartContainer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.ChestBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.ChestBlockEntity
import site.siredvin.broccolium.modules.lookup.BaseLookup
import site.siredvin.broccolium.modules.storage.base.api.AgnosticStorage
import site.siredvin.broccolium.modules.storage.item.api.*

object AgnosticItemStorageLookup : BaseLookup<AgnosticStorage<ItemStack, Int>>() {
    override fun extractFromEntity(level: Level, entity: Entity, direction: Direction?): AgnosticStorage<ItemStack, Int>? {
        val result = super.extractFromEntity(level, entity, direction)
        if (result != null) {
            return result
        }
        if (entity is Player) {
            return ContainerWrapper(entity.inventory)
        }
        if (entity is AbstractChestedHorse && entity.hasChest()) {
            return ContainerWrapper(LimitedInventory(entity.inventory, IntArray(entity.inventory.containerSize - 2) { i -> i + 2 }))
        }
        if (entity is AbstractMinecartContainer) {
            return ContainerWrapper(entity)
        }
        return null
    }

    fun extractContainerFromBlockEntity(blockEntity: BlockEntity): Container? {
        val level = blockEntity.level
        val pos = blockEntity.blockPos
        val blockState = level!!.getBlockState(pos)
        val block = blockState.block
        return if (blockEntity is Container) {
            if (blockEntity is ChestBlockEntity && block is ChestBlock) {
                ChestBlock.getContainer(block, blockState, level, pos, true)
            } else {
                blockEntity
            }
        } else {
            null
        }
    }

    override fun extractFromBlock(
        level: Level,
        pos: BlockPos,
        blockEntity: BlockEntity?,
        direction: Direction?,
    ): AgnosticStorage<ItemStack, Int>? {
        if (blockEntity is AgnosticItemStorageProvider) {
            return blockEntity.itemStorage
        }
        val result = super.extractFromBlock(level, pos, blockEntity, direction)
        if (result != null) {
            return result
        }
        if (blockEntity != null) {
            val container = extractContainerFromBlockEntity(blockEntity) ?: return null
            return ContainerWrapper(container)
        }
        return null
    }
}
