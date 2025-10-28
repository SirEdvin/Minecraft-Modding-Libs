package site.siredvin.broccolium.modules.storage.fluid

import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import site.siredvin.broccolium.modules.storage.fluid.api.*

object AgnosticFluidStorageLookup {

    private val FLUID_SINK_EXTRACTORS: MutableList<AgnosticFluidSinkExtractor> = mutableListOf()
    private val FLUID_STORAGE_EXTRACTORS: MutableList<AgnosticFluidStorageExtractor> = mutableListOf()

    private val FLUID_SINK_ENTITY_EXTRACTORS: MutableList<AgnosticFluidSinkEntityExtractor> = mutableListOf()
    private val FLUID_STORAGE_ENTITY_EXTRACTORS: MutableList<AgnosticFluidStorageEntityExtractor> = mutableListOf()

    private val FLUID_SINK_ITEM_EXTRACTORS: MutableList<AgnosticFluidSinkItemExtractor> = mutableListOf()
    private val FLUID_STORAGE_ITEM_EXTRACTORS: MutableList<AgnosticFluidStorageItemExtractor> = mutableListOf()

    fun addFluidSinkExtractor(extractor: AgnosticFluidSinkExtractor) {
        FLUID_SINK_EXTRACTORS.add(extractor)
    }

    fun addFluidStorageExtractor(extractor: AgnosticFluidStorageExtractor) {
        FLUID_STORAGE_EXTRACTORS.add(extractor)
    }

    fun addFluidSinkExtractor(extractor: AgnosticFluidSinkEntityExtractor) {
        FLUID_SINK_ENTITY_EXTRACTORS.add(extractor)
    }

    fun addFluidStorageExtractor(extractor: AgnosticFluidStorageEntityExtractor) {
        FLUID_STORAGE_ENTITY_EXTRACTORS.add(extractor)
    }

    fun addFluidSinkExtractor(extractor: AgnosticFluidSinkItemExtractor) {
        FLUID_SINK_ITEM_EXTRACTORS.add(extractor)
    }

    fun addFluidStorageExtractor(extractor: AgnosticFluidStorageItemExtractor) {
        FLUID_STORAGE_ITEM_EXTRACTORS.add(extractor)
    }

    fun extractFluidStorage(level: Level, pos: BlockPos, blockEntity: BlockEntity?): AgnosticFluidStorage? {
        if (blockEntity is AgnosticFluidStorageProvider) {
            return blockEntity.fluidStorage
        }
        for (extractor in FLUID_STORAGE_EXTRACTORS) {
            val result = extractor.extract(level, pos, blockEntity)
            if (result != null) {
                return result
            }
        }
        return null
    }

    fun extractFluidStorage(level: Level, entity: Entity): AgnosticFluidStorage? {
        for (extractor in FLUID_STORAGE_ENTITY_EXTRACTORS) {
            val result = extractor.extract(level, entity)
            if (result != null) {
                return result
            }
        }
        return null
    }

    fun extractFluidStorage(level: Level, stack: ItemStack): AgnosticFluidStorage? {
        for (extractor in FLUID_STORAGE_ITEM_EXTRACTORS) {
            val result = extractor.extract(level, stack)
            if (result != null) {
                return result
            }
        }
        return null
    }

    fun extractFluidStorageFromUnknown(level: Level, obj: Any?): AgnosticFluidStorage? {
        if (obj == null) {
            return null
        }
        if (obj is BlockPos) {
            return extractFluidStorage(level, obj, level.getBlockEntity(obj))
        }
        if (obj is BlockEntity) {
            return extractFluidStorage(level, obj.blockPos, obj)
        }
        if (obj is Entity) {
            return extractFluidStorage(level, obj)
        }
        if (obj is ItemStack) {
            return extractFluidStorage(level, obj)
        }
        throw IllegalArgumentException("Cannot extract storage for $obj")
    }

    fun extractFluidSink(level: Level, pos: BlockPos, blockEntity: BlockEntity?): AgnosticFluidSink? {
        val storage = extractFluidStorage(level, pos, blockEntity)
        if (storage != null) {
            return storage
        }

        if (blockEntity is AgnosticFluidSinkProvider) {
            return blockEntity.fluidSink
        }

        for (extractor in FLUID_SINK_EXTRACTORS) {
            val result = extractor.extract(level, pos, blockEntity)
            if (result != null) {
                return result
            }
        }
        return null
    }

    fun extractFluidSink(level: Level, entity: Entity): AgnosticFluidSink? {
        val storage = extractFluidStorage(level, entity)
        if (storage != null) {
            return storage
        }
        for (extractor in FLUID_SINK_ENTITY_EXTRACTORS) {
            val result = extractor.extract(level, entity)
            if (result != null) {
                return result
            }
        }
        return null
    }

    fun extractFluidSink(level: Level, stack: ItemStack): AgnosticFluidSink? {
        val storage = extractFluidStorage(level, stack)
        if (storage != null) {
            return storage
        }
        for (extractor in FLUID_SINK_ITEM_EXTRACTORS) {
            val result = extractor.extract(level, stack)
            if (result != null) {
                return result
            }
        }
        return null
    }

    fun extractFluidSinkFromUnknown(level: Level, obj: Any?): AgnosticFluidSink? {
        if (obj == null) {
            return null
        }
        if (obj is BlockPos) {
            return extractFluidSink(level, obj, level.getBlockEntity(obj))
        }
        if (obj is BlockEntity) {
            return extractFluidSink(level, obj.blockPos, obj)
        }
        if (obj is Entity) {
            return extractFluidSink(level, obj)
        }
        if (obj is ItemStack) {
            return extractFluidSink(level, obj)
        }
        throw IllegalArgumentException("Cannot extract storage for $obj")
    }
}
