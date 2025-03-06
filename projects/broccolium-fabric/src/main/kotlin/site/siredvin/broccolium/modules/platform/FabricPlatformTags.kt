package site.siredvin.broccolium.modules.platform

import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.Shearable
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.state.BlockState
import site.siredvin.broccolium.modules.platform.api.InnerPlatformTags

object FabricPlatformTags : InnerPlatformTags {
    override fun isOre(state: BlockState): Boolean = state.`is`(ConventionalBlockTags.ORES)

    override fun isOre(stack: ItemStack): Boolean = stack.`is`(ConventionalItemTags.ORES)

    override fun isBookshelf(state: BlockState): Boolean = state.`is`(ConventionalBlockTags.BOOKSHELVES)

    override fun isBookshelf(stack: ItemStack): Boolean = stack.`is`(ConventionalItemTags.BOOKSHELVES)

    override fun isShearable(entity: Entity, targetItem: ItemStack): Pair<Boolean, Boolean> {
        if (entity !is Shearable) {
            return Pair(false, false)
        }
        return Pair(true, entity.readyForShearing())
    }
}
