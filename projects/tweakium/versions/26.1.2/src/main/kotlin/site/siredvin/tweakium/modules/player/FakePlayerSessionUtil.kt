package site.siredvin.tweakium.modules.player

import net.minecraft.world.Container
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.ai.attributes.AttributeMap
import net.minecraft.world.item.ItemStack
import site.siredvin.broccolium.modules.storage.base.api.SlottedAgnosticStorage

internal object FakePlayerSessionUtil {
    fun borrowInventory(storage: SlottedAgnosticStorage<ItemStack, Int>, inventory: Container, returnRemainder: (ItemStack) -> Unit) {
        require(storage.size <= inventory.containerSize) { "Storage exceeds fake-player inventory capacity" }
        inventory.clearContent()
        try {
            for (slot in 0 until storage.size) {
                val expected = storage.get(slot).copy()
                if (expected.isEmpty) continue
                val extracted = storage.take(expected.count, slot, slot, { ItemStack.isSameItemSameComponents(it, expected) }, false)
                inventory.setItem(slot, extracted.copy())
                check(ItemStack.matches(expected, extracted)) { "Storage refused complete fake-player inventory extraction" }
            }
        } catch (failure: Throwable) {
            returnInventory(storage, inventory, returnRemainder)
            throw failure
        }
    }

    fun returnInventory(storage: SlottedAgnosticStorage<ItemStack, Int>, inventory: Container, returnRemainder: (ItemStack) -> Unit) {
        for (slot in 0 until inventory.containerSize) {
            val stack = inventory.getItem(slot)
            if (stack.isEmpty) continue
            val remainder = if (slot < storage.size) storage.store(stack, slot, slot, false) else stack
            if (!remainder.isEmpty) returnRemainder(remainder)
            inventory.setItem(slot, ItemStack.EMPTY)
        }
    }

    // Vanilla only permits a selected hotbar slot. Swap the owner's selected
    // stack into slot zero instead of bypassing that validation for turtles.
    // This is an involution: call again before copying the inventory back.
    fun swapSelected(inventory: Container, selected: Int) {
        require(selected in 0 until inventory.containerSize) { "Selected slot outside inventory" }
        if (selected == 0) return
        val first = inventory.getItem(0)
        inventory.setItem(0, inventory.getItem(selected))
        inventory.setItem(selected, first)
    }

    // Snapshot the actual applied IDs, not the possibly replaced/consumed tool
    // at the end of the operation. Restore preexisting modifiers on close.
    fun applyToolAttributes(stack: ItemStack, attributes: AttributeMap): AutoCloseable {
        val undo = mutableListOf<() -> Unit>()
        val scope = AutoCloseable {
            undo.asReversed().forEach { it() }
            undo.clear()
        }
        try {
            stack.forEachModifier(EquipmentSlot.MAINHAND) { attribute, modifier ->
                val instance = attributes.getInstance(attribute) ?: return@forEachModifier
                val previous = instance.getModifier(modifier.id())
                val wasPermanent = previous in instance.permanentModifiers
                undo.add {
                    instance.removeModifier(modifier.id())
                    if (previous != null) {
                        if (wasPermanent) instance.addPermanentModifier(previous) else instance.addTransientModifier(previous)
                    }
                }
                instance.removeModifier(modifier.id())
                instance.addTransientModifier(modifier)
            }
        } catch (failure: Throwable) {
            scope.close()
            throw failure
        }
        return scope
    }
}
