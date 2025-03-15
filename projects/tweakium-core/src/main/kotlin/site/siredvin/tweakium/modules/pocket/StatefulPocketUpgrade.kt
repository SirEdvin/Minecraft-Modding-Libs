package site.siredvin.tweakium.modules.pocket

import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.component.DataComponentType
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

    open val importantComponents: List<DataComponentType<*>>
        get() = listOf(DataComponents.CUSTOM_DATA)

    constructor(id: ResourceLocation, stack: ItemStack) : this(
        id,
        pocketAdjectiveComponent(id),
        stack,
    )

    override fun getUpgradeData(stack: ItemStack): DataComponentPatch {
        val builder = DataComponentPatch.builder()
        for (component in importantComponents) {
            val data = stack.get(component)
            if (data != null) {
                @Suppress("UNCHECKED_CAST")
                builder.set(component as DataComponentType<Any>, data)
            }
        }
        return builder.build()
    }

    override fun getUpgradeItem(upgradeData: DataComponentPatch): ItemStack {
        if (upgradeData.isEmpty) return craftingItem
        val base = craftingItem.copy()
        for (component in importantComponents) {
            val data = upgradeData.get(component) ?: Optional.empty()
            if (data.isPresent) {
                @Suppress("UNCHECKED_CAST")
                base.set(component as DataComponentType<Any>, data.get())
            }
        }
        return base
    }

    override fun isItemSuitable(stack: ItemStack): Boolean {
        val hasExtraData = importantComponents.any { stack.has(it) }
        if (!hasExtraData) return super.isItemSuitable(stack)
        val tweakedStack = stack.copy()
        importantComponents.forEach { tweakedStack.remove(it) }
        return super.isItemSuitable(tweakedStack)
    }
}
