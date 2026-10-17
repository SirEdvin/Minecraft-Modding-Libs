package site.siredvin.tweakium.modules.pocket

import dan200.computercraft.api.peripheral.IPeripheral
import dan200.computercraft.api.pocket.AbstractPocketUpgrade
import dan200.computercraft.api.pocket.IPocketAccess
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemStackTemplate
import site.siredvin.broccolium.modules.base.util.pocketAdjectiveComponent
import site.siredvin.tweakium.modules.peripheral.DisabledPeripheral
import site.siredvin.tweakium.modules.peripheral.api.IOwnedPeripheral

abstract class BasePocketUpgrade<T : IOwnedPeripheral<*>>(
    adjective: Component,
    stack: ItemStackTemplate,
) : AbstractPocketUpgrade(adjective, stack) {
    protected var peripheral: T? = null

    constructor(id: Identifier, stack: ItemStackTemplate) : this(
        pocketAdjectiveComponent(id),
        stack,
    )

    protected abstract fun getPeripheral(access: IPocketAccess): T
    override fun createPeripheral(access: IPocketAccess): IPeripheral? {
        peripheral = getPeripheral(access)
        return if (!peripheral!!.isEnabled) DisabledPeripheral else peripheral
    }
}
