package site.siredvin.tweakium.modules.turtle

import dan200.computercraft.api.turtle.TurtleUpgradeType
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import site.siredvin.broccolium.modules.base.util.turtleAdjective
import site.siredvin.tweakium.modules.peripheral.api.IOwnedPeripheral
import site.siredvin.tweakium.modules.pocket.StatefulPocketUpgrade

// CC:Tweaked 1.121 deprecates the item-less upgrade API; it stays the supported legacy path and keeps 2.0.x saves loading.
@Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
abstract class StatefulTurtleUpgrade<T : IOwnedPeripheral<*>>(
    id: ResourceLocation,
    type: TurtleUpgradeType,
    adjective: String,
    stack: ItemStack,
) : BaseTurtleUpgrade<T>(id, type, adjective, stack) {
    companion object {
        const val STORED_DATA_TAG = StatefulPocketUpgrade.STORED_DATA_TAG
    }

    constructor(id: ResourceLocation, type: TurtleUpgradeType, stack: ItemStack) : this(
        id,
        type,
        turtleAdjective(id),
        stack,
    )

    override fun getUpgradeData(stack: ItemStack): CompoundTag {
        return stack.getTagElement(STORED_DATA_TAG) ?: return CompoundTag()
    }

    override fun getUpgradeItem(upgradeData: CompoundTag): ItemStack {
        if (upgradeData.isEmpty) return craftingItem
        val base = craftingItem.copy()
        base.addTagElement(STORED_DATA_TAG, upgradeData)
        return base
    }

    override fun isItemSuitable(stack: ItemStack): Boolean {
        if (stack.getTagElement(STORED_DATA_TAG) == null) return super.isItemSuitable(stack)
        val tweakedStack = stack.copy()
        tweakedStack.orCreateTag.remove(STORED_DATA_TAG)
        return super.isItemSuitable(tweakedStack)
    }
}
