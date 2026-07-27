package site.siredvin.tweakium.modules.pocket

import dan200.computercraft.api.pocket.IPocketAccess
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import site.siredvin.broccolium.modules.base.util.pocketAdjective
import site.siredvin.tweakium.modules.peripheral.api.IOwnedPeripheral
import site.siredvin.tweakium.modules.pocket.api.PockerUpgradePeripheralBuilder

class PeripheralPocketUpgrade<T : IOwnedPeripheral<*>> : BasePocketUpgrade<T> {

    private val constructor: PockerUpgradePeripheralBuilder<T>

    constructor(id: ResourceLocation, adjective: String, stack: ItemStack, constructor: PockerUpgradePeripheralBuilder<T>) : super(
        id,
        adjective,
        stack,
    ) {
        this.constructor = constructor
    }

    constructor(id: ResourceLocation, stack: ItemStack, constructor: PockerUpgradePeripheralBuilder<T>) : super(
        id,
        pocketAdjective(id),
        stack,
    ) {
        this.constructor = constructor
    }

    override fun getPeripheral(access: IPocketAccess): T = constructor.build(access)
}
