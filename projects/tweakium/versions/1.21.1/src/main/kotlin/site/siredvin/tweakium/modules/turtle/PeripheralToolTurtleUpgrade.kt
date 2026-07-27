package site.siredvin.tweakium.modules.turtle

import dan200.computercraft.api.turtle.TurtleUpgradeType
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import site.siredvin.tweakium.modules.peripheral.api.IOwnedPeripheral

abstract class PeripheralToolTurtleUpgrade<T : IOwnedPeripheral<*>> : BaseTurtleUpgrade<T> {
    constructor(id: ResourceLocation, adjective: Component, item: ItemStack) : super(
        id,
        TurtleUpgradeType.BOTH,
        adjective,
        item,
    )

    constructor(id: ResourceLocation, item: ItemStack) : super(
        id,
        TurtleUpgradeType.BOTH,
        item,
    )
}
