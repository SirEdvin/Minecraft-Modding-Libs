package site.siredvin.broccolium.test

import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage
import net.minecraft.world.SimpleContainer
import net.minecraft.world.item.ItemStack
import org.junit.jupiter.api.extension.ExtendWith
import site.siredvin.broccolium.modules.storage.base.api.AccessibleAgnosticStorage
import site.siredvin.broccolium.modules.storage.base.api.SlottedAgnosticStorage
import site.siredvin.broccolium.modules.storage.item.ContainerWrapper
import site.siredvin.broccolium.modules.storage.item.FabricSlottedStorageWrapper
import site.siredvin.broccolium.modules.storage.item.FabricStorageWrapper
import site.siredvin.broccolium.test.storage.DummyStorage

internal class TweakedFabricStorageWrapper(private val inventoryStorage: InventoryStorage) :
    FabricStorageWrapper(inventoryStorage),
    AccessibleAgnosticStorage<ItemStack, Int> {
    override fun get(slot: Int): ItemStack {
        val variantInSlot = inventoryStorage.getSlot(slot)
        return variantInSlot.resource.toStack(variantInSlot.amount.toInt())
    }
}

@WithMinecraft
@ExtendWith(BroccoliumInitialization::class)
internal class FabricSlottedStorageTests : SlottedStorageTests() {

    override fun createSlottedStorage(items: List<ItemStack>, secondary: Boolean): SlottedAgnosticStorage<ItemStack, Int> {
        val container = SimpleContainer(items.size)
        items.forEachIndexed { index, itemStack ->
            if (!itemStack.isEmpty) {
                container.setItem(index, itemStack)
            }
        }
        return FabricSlottedStorageWrapper(InventoryStorage.of(container, null))
    }
}

@WithMinecraft
@ExtendWith(BroccoliumInitialization::class)
internal class FabricStorageTests : StorageTests() {

    override fun createStorage(items: List<ItemStack>, secondary: Boolean): AccessibleAgnosticStorage<ItemStack, Int> {
        val container = SimpleContainer(items.size)
        items.forEachIndexed { index, itemStack ->
            if (!itemStack.isEmpty) {
                container.setItem(index, itemStack)
            }
        }
        return TweakedFabricStorageWrapper(InventoryStorage.of(container, null))
    }
}

@WithMinecraft
@ExtendWith(BroccoliumInitialization::class)
internal class CompactFabricSlottedStorageTests : SlottedStorageTests() {

    override fun createSlottedStorage(items: List<ItemStack>, secondary: Boolean): SlottedAgnosticStorage<ItemStack, Int> {
        if (secondary) {
            val container = SimpleContainer(items.size)
            items.forEachIndexed { index, itemStack ->
                if (!itemStack.isEmpty) {
                    container.setItem(index, itemStack)
                }
            }
            return ContainerWrapper(container)
        }
        val container = SimpleContainer(items.size)
        items.forEachIndexed { index, itemStack ->
            if (!itemStack.isEmpty) {
                container.setItem(index, itemStack)
            }
        }
        return FabricSlottedStorageWrapper(InventoryStorage.of(container, null))
    }
}

@WithMinecraft
@ExtendWith(BroccoliumInitialization::class)
internal class CompactFabricStorageTests : StorageTests() {

    override fun createStorage(items: List<ItemStack>, secondary: Boolean): AccessibleAgnosticStorage<ItemStack, Int> {
        if (secondary) {
            return DummyStorage(items.size, items)
        }
        val container = SimpleContainer(items.size)
        items.forEachIndexed { index, itemStack ->
            if (!itemStack.isEmpty) {
                container.setItem(index, itemStack)
            }
        }
        return TweakedFabricStorageWrapper(InventoryStorage.of(container, null))
    }
}

@WithMinecraft
@ExtendWith(BroccoliumInitialization::class)
internal class VerificationFabricStorageTests : StorageTests() {
    override fun createStorage(items: List<ItemStack>, secondary: Boolean): AccessibleAgnosticStorage<ItemStack, Int> {
        if (secondary) {
            val container = SimpleContainer(items.size)
            items.forEachIndexed { index, itemStack ->
                if (!itemStack.isEmpty) {
                    container.setItem(index, itemStack)
                }
            }
            return FabricSlottedStorageWrapper(InventoryStorage.of(container, null))
        }
        val container = SimpleContainer(items.size)
        items.forEachIndexed { index, itemStack ->
            if (!itemStack.isEmpty) {
                container.setItem(index, itemStack)
            }
        }
        return TweakedFabricStorageWrapper(InventoryStorage.of(container, null))
    }
}

@WithMinecraft
@ExtendWith(BroccoliumInitialization::class)
internal class ReverseVerificationFabricStorageTests : StorageTests() {
    override fun createStorage(items: List<ItemStack>, secondary: Boolean): AccessibleAgnosticStorage<ItemStack, Int> {
        if (!secondary) {
            val container = SimpleContainer(items.size)
            items.forEachIndexed { index, itemStack ->
                if (!itemStack.isEmpty) {
                    container.setItem(index, itemStack)
                }
            }
            return FabricSlottedStorageWrapper(InventoryStorage.of(container, null))
        }
        val container = SimpleContainer(items.size)
        items.forEachIndexed { index, itemStack ->
            if (!itemStack.isEmpty) {
                container.setItem(index, itemStack)
            }
        }
        return TweakedFabricStorageWrapper(InventoryStorage.of(container, null))
    }
}
