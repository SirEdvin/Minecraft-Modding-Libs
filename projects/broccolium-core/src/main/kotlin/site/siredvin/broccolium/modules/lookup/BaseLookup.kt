package site.siredvin.broccolium.modules.lookup

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import site.siredvin.broccolium.modules.storage.base.api.SlottedAgnosticStorage

open class BaseLookup<T> {
    private val blockLookups: MutableList<BlockBasedLookup<T>> = mutableListOf()
    private val entityLookups: MutableList<EntityBasedLookup<T>> = mutableListOf()
    private val inventoryItemLookups: MutableList<InventoryItemBasedLookup<T, SlottedAgnosticStorage<ItemStack, Int>>> = mutableListOf()

    open fun addBlockLookup(lookup: BlockBasedLookup<T>) {
        this.blockLookups.add(lookup)
    }

    open fun addEntityLookup(lookup: EntityBasedLookup<T>) {
        this.entityLookups.add(lookup)
    }

    open fun addInventoryItemLookup(lookup: InventoryItemBasedLookup<T, SlottedAgnosticStorage<ItemStack, Int>>) {
        this.inventoryItemLookups.add(lookup)
    }

    open fun extractFromBlock(level: Level, pos: BlockPos, blockEntity: BlockEntity?, direction: Direction?): T? {
        for (extractor in blockLookups) {
            val result = extractor.extract(level, pos, blockEntity, direction)
            if (result != null) {
                return result
            }
        }
        return null
    }

    open fun extractFromEntity(level: Level, entity: Entity, direction: Direction?): T? {
        for (extractor in entityLookups) {
            val result = extractor.extract(level, entity, direction)
            if (result != null) {
                return result
            }
        }
        return null
    }

    open fun extractFromInventoryStack(level: Level, origin: SlottedAgnosticStorage<ItemStack, Int>, slot: Int): T? {
        for (extractor in inventoryItemLookups) {
            val result = extractor.extract(level, origin, slot)
            if (result != null) {
                return result
            }
        }
        return null
    }

    open fun extractFromUnknown(level: Level, obj: Any?, direction: Direction?): T? {
        if (obj == null) {
            return null
        }
        if (obj is BlockPos) {
            return extractFromBlock(level, obj, level.getBlockEntity(obj), direction)
        }
        if (obj is BlockEntity) {
            return extractFromBlock(level, obj.blockPos, obj, direction)
        }
        if (obj is Entity) {
            return extractFromEntity(level, obj, direction)
        }
        throw IllegalArgumentException("Cannot extract storage for $obj")
    }
}
