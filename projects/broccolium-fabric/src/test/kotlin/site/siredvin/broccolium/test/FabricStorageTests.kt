package site.siredvin.broccolium.test

import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage
import net.minecraft.world.SimpleContainer
import net.minecraft.world.item.ItemStack
import site.siredvin.broccolium.modules.storage.item.ContainerWrapper
import site.siredvin.broccolium.modules.storage.item.FabricSlottedStorageWrapper
import site.siredvin.broccolium.modules.storage.item.FabricStorageWrapper
import site.siredvin.broccolium.modules.storage.item.api.AccessibleAgnosticItemStorage
import site.siredvin.broccolium.modules.storage.item.api.SlottedAgnosticItemStorage
import site.siredvin.broccolium.test.storage.DummyStorage

internal class TweakedFabricStorageWrapper(private val inventoryStorage: InventoryStorage) :
    FabricStorageWrapper(inventoryStorage),
    AccessibleAgnosticItemStorage {
    override fun getItem(slot: Int): ItemStack {
        val variantInSlot = inventoryStorage.getSlot(slot)
        return variantInSlot.resource.toStack(variantInSlot.amount.toInt())
    }
}

@WithMinecraft
internal class FabricSlottedStorageTests : SlottedStorageTests() {

    override fun createSlottedStorage(items: List<ItemStack>, secondary: Boolean): SlottedAgnosticItemStorage {
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
internal class FabricStorageTests : StorageTests() {

    override fun createStorage(items: List<ItemStack>, secondary: Boolean): AccessibleAgnosticItemStorage {
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
internal class CompactFabricSlottedStorageTests : SlottedStorageTests() {

    override fun createSlottedStorage(items: List<ItemStack>, secondary: Boolean): SlottedAgnosticItemStorage {
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
internal class CompactFabricStorageTests : StorageTests() {

    override fun createStorage(items: List<ItemStack>, secondary: Boolean): AccessibleAgnosticItemStorage {
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
internal class VerificationFabricStorageTests : StorageTests() {
    override fun createStorage(items: List<ItemStack>, secondary: Boolean): AccessibleAgnosticItemStorage {
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
internal class ReverseVerificationFabricStorageTests : StorageTests() {
    override fun createStorage(items: List<ItemStack>, secondary: Boolean): AccessibleAgnosticItemStorage {
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
