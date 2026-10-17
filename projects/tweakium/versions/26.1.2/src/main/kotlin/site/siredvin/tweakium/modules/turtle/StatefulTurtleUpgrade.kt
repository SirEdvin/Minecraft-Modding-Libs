package site.siredvin.tweakium.modules.turtle

import dan200.computercraft.api.turtle.TurtleUpgradeType
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.ItemStackTemplate
import site.siredvin.broccolium.modules.base.util.turtleAdjectiveComponent
import site.siredvin.tweakium.modules.peripheral.api.IOwnedPeripheral
import site.siredvin.tweakium.modules.peripheral.util.UpgradeTemplateUtil

abstract class StatefulTurtleUpgrade<T : IOwnedPeripheral<*>>(
    id: Identifier,
    type: TurtleUpgradeType,
    adjective: Component,
    stack: ItemStackTemplate,
) : BaseTurtleUpgrade<T>(id, type, adjective, stack) {
    open val importantComponents: List<DataComponentType<*>>
        get() = listOf(DataComponents.CUSTOM_DATA)

    constructor(id: Identifier, type: TurtleUpgradeType, stack: ItemStackTemplate) : this(
        id,
        type,
        turtleAdjectiveComponent(id),
        stack,
    )

    override fun getUpgradeData(stack: ItemStackTemplate): DataComponentPatch = UpgradeTemplateUtil.read(stack, craftingItem, importantComponents)

    override fun getUpgradeItem(upgradeData: DataComponentPatch): ItemStackTemplate = UpgradeTemplateUtil.write(craftingItem, upgradeData, importantComponents)

    override fun isItemSuitable(stack: ItemStack): Boolean {
        val hasExtraData = importantComponents.any { stack.has(it) }
        if (!hasExtraData) return super.isItemSuitable(stack)
        val tweakedStack = stack.copy()
        importantComponents.forEach { tweakedStack.remove(it) }
        return super.isItemSuitable(tweakedStack)
    }
}
