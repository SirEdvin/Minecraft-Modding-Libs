package site.siredvin.tweakium.modules.turtle

import dan200.computercraft.api.turtle.TurtleUpgradeType
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import site.siredvin.broccolium.modules.base.util.turtleAdjectiveComponent
import site.siredvin.tweakium.modules.peripheral.api.IOwnedPeripheral
import java.util.*

abstract class StatefulTurtleUpgrade<T : IOwnedPeripheral<*>>(
    id: ResourceLocation,
    type: TurtleUpgradeType,
    adjective: Component,
    stack: ItemStack,
) : BaseTurtleUpgrade<T>(id, type, adjective, stack) {
    companion object {
    }

    constructor(id: ResourceLocation, type: TurtleUpgradeType, stack: ItemStack) : this(
        id,
        type,
        turtleAdjectiveComponent(id),
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
