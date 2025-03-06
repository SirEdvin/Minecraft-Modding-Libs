package site.siredvin.tweakium.modules.pocket

import dan200.computercraft.api.pocket.IPocketAccess
import dan200.computercraft.api.pocket.IPocketUpgrade
import dan200.computercraft.api.upgrades.UpgradeType
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import site.siredvin.broccolium.modules.base.util.pocketAdjectiveComponent
import site.siredvin.tweakium.modules.peripheral.api.IOwnedPeripheral
import site.siredvin.tweakium.modules.pocket.api.PockerUpgradePeripheralBuilder

class PeripheralPocketUpgrade<T : IOwnedPeripheral<*>> : BasePocketUpgrade<T> {

    private val constructor: PockerUpgradePeripheralBuilder<T>
    private val type: UpgradeType<PeripheralPocketUpgrade<T>>

    constructor(adjective: Component, stack: ItemStack, constructor: PockerUpgradePeripheralBuilder<T>, type: UpgradeType<PeripheralPocketUpgrade<T>>? = null) : super(
        adjective,
        stack,
    ) {
        this.constructor = constructor
        this.type = type ?: UpgradeType.simpleWithCustomItem { PeripheralPocketUpgrade(adjective, stack, constructor) }
    }

    constructor(id: ResourceLocation, stack: ItemStack, constructor: PockerUpgradePeripheralBuilder<T>, type: UpgradeType<PeripheralPocketUpgrade<T>>? = null) : super(
        pocketAdjectiveComponent(id),
        stack,
    ) {
        this.constructor = constructor
        this.type = type ?: UpgradeType.simpleWithCustomItem { PeripheralPocketUpgrade(adjective, stack, constructor) }
    }

    override fun getPeripheral(access: IPocketAccess): T = constructor.build(access)
    override fun getType(): UpgradeType<out IPocketUpgrade> = this.type
}
