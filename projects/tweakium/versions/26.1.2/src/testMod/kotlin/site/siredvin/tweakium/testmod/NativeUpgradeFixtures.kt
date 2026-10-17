package site.siredvin.tweakium.testmod

import dan200.computercraft.api.pocket.IPocketAccess
import dan200.computercraft.api.pocket.IPocketUpgrade
import dan200.computercraft.api.turtle.ITurtleAccess
import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.api.turtle.TurtleSide
import dan200.computercraft.api.turtle.TurtleUpgradeType
import dan200.computercraft.api.upgrades.UpgradeType
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemStackTemplate
import site.siredvin.tweakium.modules.peripheral.OwnedPeripheral
import site.siredvin.tweakium.modules.peripheral.api.IPeripheralOwner
import site.siredvin.tweakium.modules.peripheral.owner.PocketPeripheralOwner
import site.siredvin.tweakium.modules.peripheral.owner.TurtlePeripheralOwner
import site.siredvin.tweakium.modules.platform.api.InnerComputerBasePlatform
import site.siredvin.tweakium.modules.pocket.StatefulPocketUpgrade
import site.siredvin.tweakium.modules.turtle.StatefulTurtleUpgrade

object NativeUpgradeFixtures {
    val ID: Identifier = Identifier.fromNamespaceAndPath("tweakium", "r3_native")
    lateinit var turtleType: UpgradeType<Turtle>
    lateinit var pocketType: UpgradeType<Pocket>
    var configurePlayer: java.util.function.Consumer<net.minecraft.server.level.ServerPlayer> = java.util.function.Consumer { }

    @JvmStatic
    fun register(platform: InnerComputerBasePlatform) {
        turtleType = UpgradeType.simpleWithCustomItem(::Turtle)
        pocketType = UpgradeType.simpleWithCustomItem(::Pocket)
        platform.registerTurtleUpgrade(ID, turtleType)
        platform.registerPocketUpgrade(ID, pocketType)
    }

    class Peripheral(owner: IPeripheralOwner) : OwnedPeripheral<IPeripheralOwner>("r3_native", owner) {
        override val isEnabled = true
    }

    class Turtle(stack: ItemStackTemplate) : StatefulTurtleUpgrade<Peripheral>(ID, TurtleUpgradeType.PERIPHERAL, stack) {
        override fun getType(): UpgradeType<out ITurtleUpgrade> = turtleType
        override fun buildPeripheral(turtle: ITurtleAccess, side: TurtleSide): Peripheral = Peripheral(TurtlePeripheralOwner(turtle, side))
    }

    class Pocket(stack: ItemStackTemplate) : StatefulPocketUpgrade<Peripheral>(ID, stack) {
        var access: IPocketAccess? = null
            private set
        override fun getType(): UpgradeType<out IPocketUpgrade> = pocketType
        override fun getPeripheral(access: IPocketAccess): Peripheral {
            this.access = access
            return Peripheral(PocketPeripheralOwner(access))
        }
    }
}
