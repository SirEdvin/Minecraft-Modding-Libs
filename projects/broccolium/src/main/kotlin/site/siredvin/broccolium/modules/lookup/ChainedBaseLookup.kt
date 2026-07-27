package site.siredvin.broccolium.modules.lookup

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import site.siredvin.broccolium.modules.storage.base.api.SlottedAgnosticStorage

open class ChainedBaseLookup<T, V : T>(private val above: BaseLookup<V>) : BaseLookup<T>() {
    override fun extractFromBlock(level: Level, pos: BlockPos, blockEntity: BlockEntity?, direction: Direction?): T? {
        val aboveResult = above.extractFromBlock(level, pos, blockEntity, direction)
        if (aboveResult != null) {
            return aboveResult
        }
        return super.extractFromBlock(level, pos, blockEntity, direction)
    }

    override fun extractFromEntity(level: Level, entity: Entity, direction: Direction?): T? {
        val aboveResult = above.extractFromEntity(level, entity, direction)
        if (aboveResult != null) {
            return aboveResult
        }
        return super.extractFromEntity(level, entity, direction)
    }

    override fun extractFromInventoryStack(level: Level, origin: SlottedAgnosticStorage<ItemStack, Int>, slot: Int): T? {
        val aboveResult = above.extractFromInventoryStack(level, origin, slot)
        if (aboveResult != null) {
            return aboveResult
        }
        return super.extractFromInventoryStack(level, origin, slot)
    }

    override fun extractFromUnknown(level: Level, obj: Any?, direction: Direction?): T? {
        val aboveResult = above.extractFromUnknown(level, obj, direction)
        if (aboveResult != null) {
            return aboveResult
        }
        return super.extractFromUnknown(level, obj, direction)
    }
}
