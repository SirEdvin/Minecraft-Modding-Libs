package site.siredvin.broccolium.modules.storage.item

import com.google.common.collect.MapMaker
import net.fabricmc.fabric.api.transfer.v1.item.ContainerStorage
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant
import net.minecraft.world.item.ItemStack
import site.siredvin.broccolium.modules.storage.base.api.SlottedAgnosticStorage
import java.util.Collections

/** Native origins retain their own participants through FabricStorageUtils.getSlot. */
class SlottedAgnosticItemStorageWrapper(val storage: SlottedAgnosticStorage<ItemStack, Int>) :
    CombinedStorage<ItemVariant, SingleSlotStorage<ItemVariant>>(mutableListOf()),
    ContainerStorage {
    internal val replaceableStorage: ReplaceableItemStorage = storage as? ReplaceableItemStorage
        ?: throw UnsupportedOperationException("Transactional item slots require ReplaceableItemStorage; use FabricStorageUtils.getSlot for native storage")

    companion object {
        val WRAPPERS: MutableMap<SlottedAgnosticStorage<ItemStack, Int>, SlottedAgnosticItemStorageWrapper> =
            MapMaker().weakValues().makeMap<SlottedAgnosticStorage<ItemStack, Int>, SlottedAgnosticItemStorageWrapper>()

        fun of(inventory: SlottedAgnosticStorage<ItemStack, Int>): SlottedAgnosticItemStorageWrapper {
            val storage = WRAPPERS.computeIfAbsent(inventory) { SlottedAgnosticItemStorageWrapper(it) }
            storage.resizeSlotList()
            return storage
        }
    }

    val backingList: MutableList<SlottedAgnosticItemStorageSlotWrapper> = mutableListOf()
    val markDirtyParticipant: MarkDirtyParticipant = MarkDirtyParticipant(storage)

    init {
        resizeSlotList()
    }

    override fun getSlots(): MutableList<SingleSlotStorage<ItemVariant>> = parts

    private fun resizeSlotList() {
        val inventorySize = storage.size
        if (inventorySize != parts.size) {
            while (backingList.size < inventorySize) {
                backingList.add(SlottedAgnosticItemStorageSlotWrapper(this, backingList.size))
            }
            parts = Collections.unmodifiableList<SingleSlotStorage<ItemVariant>>(backingList.subList(0, inventorySize))
        }
    }

    class MarkDirtyParticipant(private val storage: SlottedAgnosticStorage<ItemStack, Int>) : SnapshotParticipant<Boolean>() {
        override fun createSnapshot(): Boolean = true

        override fun readSnapshot(snapshot: Boolean) {
        }

        override fun onFinalCommit() {
            storage.setChanged()
        }
    }
}
