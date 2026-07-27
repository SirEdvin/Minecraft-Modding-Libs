package site.siredvin.tweakium.modules.pocket

import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import site.siredvin.broccolium.modules.base.util.pocketAdjective
import site.siredvin.tweakium.modules.peripheral.api.IOwnedPeripheral

abstract class StatefulPocketUpgrade<T : IOwnedPeripheral<*>>(
    id: ResourceLocation,
    adjective: String,
    stack: ItemStack,
) : BasePocketUpgrade<T>(id, adjective, stack) {

    companion object {
        const val STORED_DATA_TAG = "storedData"
    }

    constructor(id: ResourceLocation, stack: ItemStack) : this(
        id,
        pocketAdjective(id),
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
