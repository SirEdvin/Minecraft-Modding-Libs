package site.siredvin.broccolium.modules.platform

import net.minecraft.world.entity.Entity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.common.IShearable
import net.neoforged.neoforge.common.Tags
import site.siredvin.broccolium.modules.platform.api.InnerPlatformTags

object ForgePlatformTags : InnerPlatformTags {
    override fun isOre(state: BlockState): Boolean = state.`is`(Tags.Blocks.ORES)

    override fun isOre(stack: ItemStack): Boolean = stack.`is`(Tags.Items.ORES)

    override fun isBookshelf(state: BlockState): Boolean = state.`is`(Tags.Blocks.BOOKSHELVES)

    override fun isBookshelf(stack: ItemStack): Boolean = stack.`is`(Tags.Items.BOOKSHELVES)

    override fun isShearable(entity: Entity, targetItem: ItemStack): Pair<Boolean, Boolean> {
        if (entity is IShearable) {
            return Pair(true, entity.isShearable(null, targetItem, entity.level(), entity.blockPosition()))
        }
        return Pair(false, false)
    }
}
