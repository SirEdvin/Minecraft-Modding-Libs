package site.siredvin.tweakium.modules.pocket

import dan200.computercraft.api.peripheral.IPeripheral
import dan200.computercraft.api.pocket.AbstractPocketUpgrade
import dan200.computercraft.api.pocket.IPocketAccess
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import site.siredvin.broccolium.modules.base.util.pocketAdjective
import site.siredvin.tweakium.modules.peripheral.DisabledPeripheral
import site.siredvin.tweakium.modules.peripheral.api.IOwnedPeripheral

// CC:Tweaked 1.121 deprecates the item-less upgrade API; it stays the supported legacy path and keeps 2.0.x saves loading.
@Suppress("DEPRECATION")
abstract class BasePocketUpgrade<T : IOwnedPeripheral<*>>(
    id: ResourceLocation,
    adjective: String = pocketAdjective(id),
    stack: ItemStack,
) : AbstractPocketUpgrade(id, adjective, stack) {
    protected var peripheral: T? = null

    constructor(id: ResourceLocation, stack: ItemStack) : this(
        id,
        pocketAdjective(id),
        stack,
    )

    protected abstract fun getPeripheral(access: IPocketAccess): T
    override fun createPeripheral(access: IPocketAccess): IPeripheral? {
        peripheral = getPeripheral(access)
        return if (!peripheral!!.isEnabled) DisabledPeripheral else peripheral
    }
}
