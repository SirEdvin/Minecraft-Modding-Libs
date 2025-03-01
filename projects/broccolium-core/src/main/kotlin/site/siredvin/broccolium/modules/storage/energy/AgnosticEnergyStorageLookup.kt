package site.siredvin.broccolium.modules.storage.energy

import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import site.siredvin.broccolium.modules.storage.energy.api.*

object AgnosticEnergyStorageLookup {

    private val ENERGY_SINK_EXTRACTORS: MutableList<AgnosticEnergySinkExtractor> = mutableListOf()
    private val ENERGY_STORAGE_EXTRACTORS: MutableList<AgnosticEnergyStorageExtractor> = mutableListOf()

    private val ENERGY_SINK_ENTITY_EXTRACTORS: MutableList<AgnosticEnergySinkEntityExtractor> = mutableListOf()
    private val ENERGY_STORAGE_ENTITY_EXTRACTORS: MutableList<AgnosticEnergyStorageEntityExtractor> = mutableListOf()

    private val ENERGY_SINK_ITEM_EXTRACTORS: MutableList<AgnosticEnergySinkItemExtractor> = mutableListOf()
    private val ENERGY_STORAGE_ITEM_EXTRACTORS: MutableList<AgnosticEnergyStorageItemExtractor> = mutableListOf()

    fun addEnergySinkExtractor(extractor: AgnosticEnergySinkExtractor) {
        ENERGY_SINK_EXTRACTORS.add(extractor)
    }

    fun addEnergyStorageExtractor(extractor: AgnosticEnergyStorageExtractor) {
        ENERGY_STORAGE_EXTRACTORS.add(extractor)
    }

    fun addEnergySinkExtractor(extractor: AgnosticEnergySinkEntityExtractor) {
        ENERGY_SINK_ENTITY_EXTRACTORS.add(extractor)
    }

    fun addEnergyStorageExtractor(extractor: AgnosticEnergyStorageEntityExtractor) {
        ENERGY_STORAGE_ENTITY_EXTRACTORS.add(extractor)
    }

    fun addEnergySinkExtractor(extractor: AgnosticEnergySinkItemExtractor) {
        ENERGY_SINK_ITEM_EXTRACTORS.add(extractor)
    }

    fun addEnergyStorageExtractor(extractor: AgnosticEnergyStorageItemExtractor) {
        ENERGY_STORAGE_ITEM_EXTRACTORS.add(extractor)
    }

    fun extractEnergyStorage(level: Level, pos: BlockPos, blockEntity: BlockEntity?): AgnosticEnergyStorage? {
        for (extractor in ENERGY_STORAGE_EXTRACTORS) {
            val result = extractor.extract(level, pos, blockEntity)
            if (result != null) {
                return result
            }
        }

        return null
    }

    fun extractEnergyStorage(level: Level, entity: Entity): AgnosticEnergyStorage? {
        for (extractor in ENERGY_STORAGE_ENTITY_EXTRACTORS) {
            val result = extractor.extract(level, entity)
            if (result != null) {
                return result
            }
        }
        return null
    }

    fun extractEnergyStorage(level: Level, stack: ItemStack): AgnosticEnergyStorage? {
        for (extractor in ENERGY_STORAGE_ITEM_EXTRACTORS) {
            val result = extractor.extract(level, stack)
            if (result != null) {
                return result
            }
        }
        return null
    }

    fun extractEnergyStorageFromUnknown(level: Level, obj: Any?): AgnosticEnergyStorage? {
        if (obj == null) {
            return null
        }
        if (obj is BlockPos) {
            return extractEnergyStorage(level, obj, level.getBlockEntity(obj))
        }
        if (obj is BlockEntity) {
            return extractEnergyStorage(level, obj.blockPos, obj)
        }
        if (obj is Entity) {
            return extractEnergyStorage(level, obj)
        }
        if (obj is ItemStack) {
            return extractEnergyStorage(level, obj)
        }
        throw IllegalArgumentException("Cannot extract storage for $obj")
    }

    fun extractEnergySink(level: Level, pos: BlockPos, blockEntity: BlockEntity?): AgnosticEnergySink? {
        val storage = extractEnergyStorage(level, pos, blockEntity)
        if (storage != null) {
            return storage
        }

        for (extractor in ENERGY_SINK_EXTRACTORS) {
            val result = extractor.extract(level, pos, blockEntity)
            if (result != null) {
                return result
            }
        }
        return null
    }

    fun extractEnergySink(level: Level, entity: Entity): AgnosticEnergySink? {
        val storage = extractEnergyStorage(level, entity)
        if (storage != null) {
            return storage
        }
        for (extractor in ENERGY_SINK_ENTITY_EXTRACTORS) {
            val result = extractor.extract(level, entity)
            if (result != null) {
                return result
            }
        }
        return null
    }

    fun extractEnergySink(level: Level, stack: ItemStack): AgnosticEnergySink? {
        val storage = extractEnergyStorage(level, stack)
        if (storage != null) {
            return storage
        }
        for (extractor in ENERGY_SINK_ITEM_EXTRACTORS) {
            val result = extractor.extract(level, stack)
            if (result != null) {
                return result
            }
        }
        return null
    }

    fun extractEnergySinkFromUnknown(level: Level, obj: Any?): AgnosticEnergySink? {
        if (obj == null) {
            return null
        }
        if (obj is BlockPos) {
            return extractEnergySink(level, obj, level.getBlockEntity(obj))
        }
        if (obj is BlockEntity) {
            return extractEnergySink(level, obj.blockPos, obj)
        }
        if (obj is Entity) {
            return extractEnergySink(level, obj)
        }
        if (obj is ItemStack) {
            return extractEnergySink(level, obj)
        }
        throw IllegalArgumentException("Cannot extract storage for $obj")
    }
}
