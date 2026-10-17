package site.siredvin.tweakium.modules.turtle

import dan200.computercraft.api.turtle.TurtleUpgradeType
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemStackTemplate
import site.siredvin.tweakium.modules.peripheral.api.IOwnedPeripheral

abstract class PeripheralToolTurtleUpgrade<T : IOwnedPeripheral<*>> : BaseTurtleUpgrade<T> {
    constructor(id: Identifier, adjective: Component, item: ItemStackTemplate) : super(id, TurtleUpgradeType.BOTH, adjective, item)
    constructor(id: Identifier, item: ItemStackTemplate) : super(id, TurtleUpgradeType.BOTH, item)
}
