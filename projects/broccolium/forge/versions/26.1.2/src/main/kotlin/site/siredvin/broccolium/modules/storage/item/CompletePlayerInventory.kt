package site.siredvin.broccolium.modules.storage.item

import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.neoforged.neoforge.transfer.CombinedResourceHandler
import net.neoforged.neoforge.transfer.ResourceHandler
import net.neoforged.neoforge.transfer.item.ItemResource
import net.neoforged.neoforge.transfer.item.ItemStackResourceHandler
import net.neoforged.neoforge.transfer.item.PlayerInventoryWrapper
import net.neoforged.neoforge.transfer.transaction.TransactionContext
import java.lang.ref.WeakReference
import java.util.WeakHashMap

/** Adds the equipment slots omitted by NeoForge's default player capability. */
internal object CompletePlayerInventory {
    // Share journals across repeated lookups, without retaining disconnected players.
    private val wrappers = WeakHashMap<Player, WeakReference<ResourceHandler<ItemResource>>>()

    fun of(player: Player, main: PlayerInventoryWrapper): ResourceHandler<ItemResource> {
        wrappers[player]?.get()?.let { return it }
        val extra = (main.size() until player.inventory.containerSize).map { index ->
            val slot = checkNotNull(Inventory.EQUIPMENT_SLOT_MAPPING[index])
            object : ItemStackResourceHandler() {
                override fun getStack(): ItemStack = player.inventory.getItem(index)

                override fun setStack(stack: ItemStack) {
                    player.inventory.setItem(index, stack, true)
                }

                override fun isValid(resource: ItemResource): Boolean = resource.toStack().canEquip(slot, player)

                override fun getCapacity(resource: ItemResource): Int = slot.countLimit

                override fun extract(index: Int, resource: ItemResource, amount: Int, transaction: TransactionContext): Int {
                    if (!player.isCreative && EnchantmentHelper.has(getStack(), EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE)) return 0
                    return super.extract(index, resource, amount, transaction)
                }

                override fun onRootCommit(originalState: ItemStack) {
                    player.onEquipItem(slot, originalState, getStack())
                    player.inventory.setChanged()
                    player.containerMenu.broadcastChanges()
                }
            }
        }
        return CombinedResourceHandler(main, *extra.toTypedArray()).also { wrappers[player] = WeakReference(it) }
    }
}
