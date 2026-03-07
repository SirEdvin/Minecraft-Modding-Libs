package site.siredvin.broccolium.modules.base.util.world

import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB
import java.util.function.BiConsumer
import java.util.function.Predicate

object ScanUtils {

    fun getBox(pos: BlockPos, radius: Double): AABB {
        val center = pos.center
        val r = radius + 0.5

        return AABB(
            center.x - r,
            center.y - r,
            center.z - r,
            center.x + r,
            center.y + r,
            center.z + r,
        ).inflate(0.99)
    }

    fun traverseBlocks(
        world: Level,
        center: BlockPos,
        radius: Int,
        consumer: BiConsumer<BlockState, BlockPos>,
        relativePosition: Boolean = false,
        predicate: Predicate<BlockState> = Predicate { !it.isAir },
    ) {
        val x = center.x
        val y = center.y
        val z = center.z
        for (oX in x - radius..x + radius) {
            for (oY in y - radius..y + radius) {
                for (oZ in z - radius..z + radius) {
                    val subPos = BlockPos(oX, oY, oZ)
                    val blockState = world.getBlockState(subPos)
                    if (predicate.test(blockState)) {
                        if (relativePosition) {
                            consumer.accept(blockState, BlockPos(oX - x, oY - y, oZ - z))
                        } else {
                            consumer.accept(blockState, subPos)
                        }
                    }
                }
            }
        }
    }
}
