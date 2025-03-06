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

class StatefulPeripheralPocketUpgrade<T : IOwnedPeripheral<*>>(
    id: ResourceLocation,
    adjective: Component,
    stack: ItemStack,
    private val constructor: PockerUpgradePeripheralBuilder<T>,
    type: UpgradeType<StatefulPeripheralPocketUpgrade<T>>? = null,
) : StatefulPocketUpgrade<T>(id, adjective, stack) {

    private val type: UpgradeType<StatefulPeripheralPocketUpgrade<T>> = type ?: UpgradeType.simpleWithCustomItem { StatefulPeripheralPocketUpgrade(id, adjective, stack, constructor) }

    constructor(id: ResourceLocation, stack: ItemStack, constructor: PockerUpgradePeripheralBuilder<T>, type: UpgradeType<StatefulPeripheralPocketUpgrade<T>>? = null) : this(
        id,
        pocketAdjectiveComponent(id),
        stack,
        constructor,
        type,
    )

    override fun getPeripheral(access: IPocketAccess): T = constructor.build(access)

    override fun getType(): UpgradeType<out IPocketUpgrade> = type
}
