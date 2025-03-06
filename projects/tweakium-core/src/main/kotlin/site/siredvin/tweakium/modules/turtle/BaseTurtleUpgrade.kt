package site.siredvin.tweakium.modules.turtle

import dan200.computercraft.api.peripheral.IPeripheral
import dan200.computercraft.api.turtle.AbstractTurtleUpgrade
import dan200.computercraft.api.turtle.ITurtleAccess
import dan200.computercraft.api.turtle.TurtleSide
import dan200.computercraft.api.turtle.TurtleUpgradeType
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import site.siredvin.broccolium.modules.base.util.turtleAdjectiveComponent
import site.siredvin.tweakium.modules.peripheral.DisabledPeripheral
import site.siredvin.tweakium.modules.peripheral.api.IOwnedPeripheral

abstract class BaseTurtleUpgrade<T : IOwnedPeripheral<*>>(
    id: ResourceLocation,
    type: TurtleUpgradeType,
    adjective: Component,
    stack: ItemStack,
) : AbstractTurtleUpgrade(type, adjective, stack) {

    protected abstract fun buildPeripheral(turtle: ITurtleAccess, side: TurtleSide): T

    constructor(id: ResourceLocation, type: TurtleUpgradeType, stack: ItemStack) : this(
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
