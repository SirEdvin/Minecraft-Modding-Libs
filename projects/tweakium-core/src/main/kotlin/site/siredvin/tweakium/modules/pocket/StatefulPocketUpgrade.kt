package site.siredvin.tweakium.modules.pocket

import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import site.siredvin.broccolium.modules.base.util.pocketAdjectiveComponent
import site.siredvin.tweakium.modules.peripheral.api.IOwnedPeripheral
import java.util.*

abstract class StatefulPocketUpgrade<T : IOwnedPeripheral<*>>(
    id: ResourceLocation,
    adjective: Component,
    stack: ItemStack,
) : BasePocketUpgrade<T>(adjective, stack) {

    companion object {
    }

    constructor(id: ResourceLocation, stack: ItemStack) : this(
        id,
        pocketAdjectiveComponent(id),
        stack,
    )

    override fun getUpgradeData(stack: ItemStack): DataComponentPatch {
        val customData = stack.componentsPatch.get(DataComponents.CUSTOM_DATA) ?: Optional.empty()
        if (customData.isEmpty) return DataComponentPatch.EMPTY
        return DataComponentPatch.builder().set(DataComponents.CUSTOM_DATA, customData.get()).build()
    }

    override fun getUpgradeItem(upgradeData: DataComponentPatch): ItemStack {
        if (upgradeData.isEmpty) return craftingItem
        val base = craftingItem.copy()
        val customData = upgradeData.get(DataComponents.CUSTOM_DATA) ?: Optional.empty()
        if (customData.isPresent) {
            base.applyComponents(DataComponentPatch.builder().set(DataComponents.CUSTOM_DATA, customData.get()).build())
        }
        return base
    }

    override fun isItemSuitable(stack: ItemStack): Boolean {
        val customData = stack.componentsPatch.get(DataComponents.CUSTOM_DATA) ?: Optional.empty()
        if (customData.isEmpty || customData.get().isEmpty) return super.isItemSuitable(stack)
        val tweakedStack = stack.copy()
        tweakedStack.applyComponents(DataComponentPatch.builder().set(DataComponents.CUSTOM_DATA, customData.get()).build())
        return super.isItemSuitable(tweakedStack)
    }
}
