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
import java.util.function.Supplier

open class StatefulPeripheralPocketUpgrade<T : IOwnedPeripheral<*>>(
    id: ResourceLocation,
    adjective: Component,
    stack: ItemStack,
    private val typeSup: Supplier<UpgradeType<StatefulPeripheralPocketUpgrade<T>>>,
    private val constructor: PockerUpgradePeripheralBuilder<T>,
) : StatefulPocketUpgrade<T>(id, adjective, stack) {

    constructor(id: ResourceLocation, stack: ItemStack, constructor: PockerUpgradePeripheralBuilder<T>, typeSup: Supplier<UpgradeType<StatefulPeripheralPocketUpgrade<T>>>) : this(
        id,
        pocketAdjectiveComponent(id),
        stack,
        typeSup,
        constructor,
    )

    override fun getPeripheral(access: IPocketAccess): T = constructor.build(access)

    override fun getType(): UpgradeType<out IPocketUpgrade> = typeSup.get()
}
