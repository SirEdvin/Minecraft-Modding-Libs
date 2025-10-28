package site.siredvin.broccolium.modules.storage.item

import net.minecraft.core.BlockPos
import net.minecraft.world.Container
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.vehicle.AbstractMinecartContainer
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.ChestBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.ChestBlockEntity
import site.siredvin.broccolium.modules.storage.item.api.*

object AgnosticItemStorageLookup {

    private val ADDITIONAL_ITEM_SINK_EXTRACTOR: MutableList<AgnosticItemSinkExtractor> = mutableListOf()
    private val ADDITIONAL_ITEM_STORAGE_EXTRACTORS: MutableList<AgnosticItemStorageExtractor> = mutableListOf()

    private val ADDITIONAL_ITEM_SINK_ENTITY_EXTRACTORS: MutableList<AgnosticItemSinkEntityExtractor> = mutableListOf()
    private val ADDITIONAL_STORAGE_ENTITY_EXTRACTORS: MutableList<AgnosticItemStorageEntityExtractor> = mutableListOf()

    fun addItemSinkExtractor(extractor: AgnosticItemSinkExtractor) {
        ADDITIONAL_ITEM_SINK_EXTRACTOR.add(extractor)
    }

    fun addItemStorageExtractor(extractor: AgnosticItemStorageExtractor) {
        ADDITIONAL_ITEM_STORAGE_EXTRACTORS.add(extractor)
    }

    fun addItemSinkExtractor(extractor: AgnosticItemSinkEntityExtractor) {
        ADDITIONAL_ITEM_SINK_ENTITY_EXTRACTORS.add(extractor)
    }

    fun addItemStorageExtractor(extractor: AgnosticItemStorageEntityExtractor) {
        ADDITIONAL_STORAGE_ENTITY_EXTRACTORS.add(extractor)
    }

    /**
     * Copied from old CC:T InventoryUtil because of nasty double chest hack logic
     */
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

    fun extractStorage(level: Level, pos: BlockPos, blockEntity: BlockEntity?): AgnosticItemStorage? {
        if (blockEntity is AgnosticItemStorageProvider) {
            return blockEntity.itemStorage
        }
        for (extractor in ADDITIONAL_ITEM_STORAGE_EXTRACTORS) {
            val result = extractor.extract(level, pos, blockEntity)
            if (result != null) {
                return result
            }
        }

        if (blockEntity != null) {
            if (blockEntity.isRemoved) {
                return null
            }
            val container = extractContainerFromBlockEntity(blockEntity)
            if (container != null) {
                return ContainerWrapper(container)
            }
        }
        if (blockEntity is Container) {
            return ContainerWrapper(blockEntity)
        }
        return null
    }

    fun extractStorage(level: Level, entity: Entity): AgnosticItemStorage? {
        if (entity.isRemoved) return null

        for (extractor in ADDITIONAL_STORAGE_ENTITY_EXTRACTORS) {
            val result = extractor.extract(level, entity)
            if (result != null) {
                return result
            }
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

    fun extractStorageFromUnknown(level: Level, obj: Any?): AgnosticItemStorage? {
        if (obj == null) {
            return null
        }
        if (obj is BlockPos) {
            return extractStorage(level, obj, level.getBlockEntity(obj))
        }
        if (obj is BlockEntity) {
            return extractStorage(level, obj.blockPos, obj)
        }
        if (obj is Entity) {
            return extractStorage(level, obj)
        }
        throw IllegalArgumentException("Cannot extract storage for $obj")
    }

    fun extractItemSink(level: Level, pos: BlockPos, blockEntity: BlockEntity?): AgnosticItemSink? {
        val storage = extractStorage(level, pos, blockEntity)
        if (storage != null) {
            return storage
        }
        if (blockEntity is AgnosticItemSinkProvider) {
            return blockEntity.itemSink
        }

        for (extractor in ADDITIONAL_ITEM_SINK_EXTRACTOR) {
            val result = extractor.extract(level, pos, blockEntity)
            if (result != null) {
                return result
            }
        }
        return null
    }

    fun extractItemSink(level: Level, entity: Entity): AgnosticItemSink? {
        val storage = extractStorage(level, entity)
        if (storage != null) {
            return storage
        }
        for (extractor in ADDITIONAL_ITEM_SINK_ENTITY_EXTRACTORS) {
            val result = extractor.extract(level, entity)
            if (result != null) {
                return result
            }
        }
        return null
    }

    fun extractItemSinkFromUnknown(level: Level, obj: Any?): AgnosticItemSink? {
        if (obj == null) {
            return null
        }
        if (obj is BlockPos) {
            return extractItemSink(level, obj, level.getBlockEntity(obj))
        }
        if (obj is BlockEntity) {
            return extractItemSink(level, obj.blockPos, obj)
        }
        if (obj is Entity) {
            return extractItemSink(level, obj)
        }
        throw IllegalArgumentException("Cannot extract storage for $obj")
    }
}
