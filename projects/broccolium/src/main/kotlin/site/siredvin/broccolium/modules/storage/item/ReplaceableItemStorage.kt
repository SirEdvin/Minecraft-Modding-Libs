package site.siredvin.broccolium.modules.storage.item

import net.minecraft.world.item.ItemStack
import site.siredvin.broccolium.modules.storage.base.api.SlottedAgnosticStorage

/**
 * Opt-in backing contract for transactional item-slot adapters, not an insertion operation.
 * Ordinary [SlottedAgnosticStorage.store] is additive and cannot restore a snapshot.
 *
 * Implement only when every slot can be replaced exactly, including clearing it, changing its
 * variant/data and restoring a previously observed stack even if [canPlace] now rejects it.
 * This contract covers counts from zero through the smaller of [maxStackSize] and the stack's
 * own maximum; invalid replacements must fail before writing. Adapters must refuse to snapshot
 * an out-of-range existing stack before
 * mutating it, since its restoration is not guaranteed.
 * Replacement must not merge, truncate, reject a remainder, or modify other slots. The implementation
 * must copy the supplied stack and never mutate the caller's value. Later caller mutations must
 * not change the stored contents, and later replacements must not change earlier snapshots.
 * [get] may return a live stack or a copy; adapters must not rely on live references.
 *
 * Opting in also permits extraction of any stored item. Normal insertion admission is described by
 * [canPlace], [getLimit], [maxStackSize] and the stack's own maximum size. Storages with additional
 * transfer restrictions or non-restorable side effects must not opt in. Contents/slot layout must
 * not be independently modified while a transaction is open, nor may [maxStackSize] change.
 *
 * A transactional adapter calls [setChanged] after final commit. Replacement itself may notify
 * observers (for example a vanilla Container setter); notifications are not rolled back.
 */
interface ReplaceableItemStorage : SlottedAgnosticStorage<ItemStack, Int> {
    /** Replace a valid slot's entire contents with an independent copy, bypassing insertion policy. */
    fun replaceSlot(slot: Int, stack: ItemStack)
}
