package site.siredvin.tweakium.modules.turtle

import dan200.computercraft.api.turtle.ITurtleAccess
import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.api.turtle.TurtleSide
import dan200.computercraft.api.turtle.TurtleUpgradeType
import dan200.computercraft.api.upgrades.UpgradeType
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import site.siredvin.tweakium.modules.peripheral.api.IOwnedPeripheral
import site.siredvin.tweakium.modules.turtle.api.TurtleUpgradeIDSupplier
import site.siredvin.tweakium.modules.turtle.api.TurtleUpgradePeripheralBuilder

abstract class StatefulPeripheralTurtleUpgrade<T : IOwnedPeripheral<*>> : StatefulTurtleUpgrade<T> {
    companion object {
        fun <T : IOwnedPeripheral<*>> dynamic(item: Item, constructor: TurtleUpgradePeripheralBuilder<T>, idBuilder: TurtleUpgradeIDSupplier): StatefulPeripheralTurtleUpgrade<T> = Dynamic(idBuilder.get(item), item.defaultInstance, constructor)
    }
    constructor(id: ResourceLocation, adjective: Component, item: ItemStack) : super(
        id,
        TurtleUpgradeType.PERIPHERAL,
        adjective,
        item,
    )

    constructor(id: ResourceLocation, item: ItemStack) : super(
        id,
        TurtleUpgradeType.PERIPHERAL,
        item,
    )

    private class Dynamic<T : IOwnedPeripheral<*>>(
        turtleID: ResourceLocation,
        stack: ItemStack,
        private val constructor: TurtleUpgradePeripheralBuilder<T>,
        type: UpgradeType<Dynamic<T>>? = null,
    ) : StatefulPeripheralTurtleUpgrade<T>(turtleID, stack) {

        private val type: UpgradeType<Dynamic<T>> = type ?: UpgradeType.simpleWithCustomItem { stack -> Dynamic(turtleID, stack, constructor) }

        override fun buildPeripheral(turtle: ITurtleAccess, side: TurtleSide): T = constructor.build(turtle, side)
        override fun getType(): UpgradeType<out ITurtleUpgrade> = type
    }
}
