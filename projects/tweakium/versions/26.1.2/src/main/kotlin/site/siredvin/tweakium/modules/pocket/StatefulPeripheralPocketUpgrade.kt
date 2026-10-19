package site.siredvin.tweakium.modules.pocket

import dan200.computercraft.api.pocket.IPocketAccess
import dan200.computercraft.api.pocket.IPocketUpgrade
import dan200.computercraft.api.upgrades.UpgradeType
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemStackTemplate
import site.siredvin.broccolium.modules.base.util.pocketAdjectiveComponent
import site.siredvin.tweakium.modules.peripheral.api.IOwnedPeripheral
import site.siredvin.tweakium.modules.pocket.api.PockerUpgradePeripheralBuilder
import java.util.function.Supplier

open class StatefulPeripheralPocketUpgrade<T : IOwnedPeripheral<*>>(
    id: Identifier,
    adjective: Component,
    stack: ItemStackTemplate,
    private val typeSup: Supplier<UpgradeType<StatefulPeripheralPocketUpgrade<T>>>,
    private val constructor: PockerUpgradePeripheralBuilder<T>,
) : StatefulPocketUpgrade<T>(id, adjective, stack) {
    constructor(id: Identifier, stack: ItemStackTemplate, constructor: PockerUpgradePeripheralBuilder<T>, typeSup: Supplier<UpgradeType<StatefulPeripheralPocketUpgrade<T>>>) : this(
        id,
        pocketAdjectiveComponent(id),
        stack,
        typeSup,
        constructor,
    )

    override fun getPeripheral(access: IPocketAccess): T = constructor.build(access)
    override fun getType(): UpgradeType<out IPocketUpgrade> = typeSup.get()
}
