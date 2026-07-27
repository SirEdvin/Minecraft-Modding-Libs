package site.siredvin.broccolium.modules.platform.api

import net.minecraft.world.entity.Entity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.state.BlockState

interface InnerPlatformTags {
    fun isOre(state: BlockState): Boolean

    fun isOre(stack: ItemStack): Boolean

    fun isBookshelf(state: BlockState): Boolean

    fun isBookshelf(stack: ItemStack): Boolean

    /**
     * Check if entity is shearable and is sherable now
     *
     * @return Pair of "is shearable" and "is shearable now" flags
     */
    fun isShearable(entity: Entity, targetItem: ItemStack): Pair<Boolean, Boolean>
}
