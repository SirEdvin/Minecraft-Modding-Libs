package site.siredvin.tweakium.modules.turtle

import dan200.computercraft.api.turtle.ITurtleAccess
import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.api.turtle.TurtleSide
import dan200.computercraft.api.turtle.TurtleUpgradeType
import dan200.computercraft.api.upgrades.UpgradeType
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStackTemplate
import site.siredvin.tweakium.modules.peripheral.api.IOwnedPeripheral
import site.siredvin.tweakium.modules.turtle.api.TurtleUpgradeIDSupplier
import site.siredvin.tweakium.modules.turtle.api.TurtleUpgradePeripheralBuilder
import java.util.function.Supplier

abstract class PeripheralTurtleUpgrade<T : IOwnedPeripheral<*>> : BaseTurtleUpgrade<T> {
    constructor(id: Identifier, adjective: Component, item: ItemStackTemplate) : super(id, TurtleUpgradeType.PERIPHERAL, adjective, item)
    constructor(id: Identifier, item: ItemStackTemplate) : super(id, TurtleUpgradeType.PERIPHERAL, item)

    companion object {
        fun <T : IOwnedPeripheral<*>> dynamic(item: Item, constructor: TurtleUpgradePeripheralBuilder<T>, typeSup: Supplier<UpgradeType<PeripheralTurtleUpgrade<T>>>, idBuilder: TurtleUpgradeIDSupplier): PeripheralTurtleUpgrade<T> = Dynamic(idBuilder.get(item), ItemStackTemplate(item), typeSup, constructor)
    }

    private class Dynamic<T : IOwnedPeripheral<*>>(
        turtleID: Identifier,
        stack: ItemStackTemplate,
        private val typeSup: Supplier<UpgradeType<PeripheralTurtleUpgrade<T>>>,
        private val constructor: TurtleUpgradePeripheralBuilder<T>,
    ) : PeripheralTurtleUpgrade<T>(turtleID, stack) {
        override fun buildPeripheral(turtle: ITurtleAccess, side: TurtleSide): T = constructor.build(turtle, side)
        override fun getType(): UpgradeType<out ITurtleUpgrade> = typeSup.get()
    }
}
