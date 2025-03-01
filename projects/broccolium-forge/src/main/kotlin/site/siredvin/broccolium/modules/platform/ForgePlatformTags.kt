package site.siredvin.broccolium.modules.platform

import net.minecraft.world.entity.Entity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.common.IForgeShearable
import net.minecraftforge.common.Tags
import site.siredvin.broccolium.modules.platform.api.InnerPlatformTags

object ForgePlatformTags : InnerPlatformTags {
    override fun isOre(state: BlockState): Boolean = state.`is`(Tags.Blocks.ORES)

    override fun isOre(stack: ItemStack): Boolean = stack.`is`(Tags.Items.ORES)

    override fun isBookshelf(state: BlockState): Boolean = state.`is`(Tags.Blocks.BOOKSHELVES)

    override fun isBookshelf(stack: ItemStack): Boolean = stack.`is`(Tags.Items.BOOKSHELVES)

    override fun isShearable(entity: Entity, targetItem: ItemStack): Pair<Boolean, Boolean> {
        if (entity is IForgeShearable) {
            return Pair(true, entity.isShearable(targetItem, entity.level(), entity.blockPosition()))
        }
        return Pair(false, false)
    }
}
