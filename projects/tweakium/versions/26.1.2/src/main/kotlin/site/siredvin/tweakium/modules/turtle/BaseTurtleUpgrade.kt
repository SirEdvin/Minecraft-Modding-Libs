package site.siredvin.tweakium.modules.turtle

import dan200.computercraft.api.peripheral.IPeripheral
import dan200.computercraft.api.turtle.AbstractTurtleUpgrade
import dan200.computercraft.api.turtle.ITurtleAccess
import dan200.computercraft.api.turtle.TurtleSide
import dan200.computercraft.api.turtle.TurtleUpgradeType
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemStackTemplate
import site.siredvin.broccolium.modules.base.util.turtleAdjectiveComponent
import site.siredvin.tweakium.modules.peripheral.DisabledPeripheral
import site.siredvin.tweakium.modules.peripheral.api.IOwnedPeripheral

abstract class BaseTurtleUpgrade<T : IOwnedPeripheral<*>>(
    id: Identifier,
    type: TurtleUpgradeType,
    adjective: Component,
    stack: ItemStackTemplate,
) : AbstractTurtleUpgrade(type, adjective, stack) {

    protected abstract fun buildPeripheral(turtle: ITurtleAccess, side: TurtleSide): T

    constructor(id: Identifier, type: TurtleUpgradeType, stack: ItemStackTemplate) : this(
        id,
        type,
        turtleAdjectiveComponent(id),
        stack,
    )

    override fun createPeripheral(turtle: ITurtleAccess, side: TurtleSide): IPeripheral? {
        val peripheral = buildPeripheral(turtle, side)
        return if (!peripheral.isEnabled) {
            DisabledPeripheral
        } else {
            peripheral
        }
    }
}
