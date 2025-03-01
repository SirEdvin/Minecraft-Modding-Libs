package site.siredvin.broccolium.test

import net.minecraft.world.SimpleContainer
import net.minecraft.world.item.ItemStack
import site.siredvin.broccolium.modules.storage.item.ContainerWrapper
import site.siredvin.broccolium.modules.storage.item.api.AccessibleAgnosticItemStorage
import site.siredvin.broccolium.modules.storage.item.api.SlottedAgnosticItemStorage
import site.siredvin.broccolium.test.storage.DummyStorage

@WithMinecraft
internal class BaseStorageTests : StorageTests() {
    override fun createStorage(items: List<ItemStack>, secondary: Boolean): AccessibleAgnosticItemStorage = DummyStorage(items.size, items)
}

@WithMinecraft
internal class BaseSlottedStorageTests : SlottedStorageTests() {
    override fun createSlottedStorage(items: List<ItemStack>, secondary: Boolean): SlottedAgnosticItemStorage {
        val container = SimpleContainer(items.size)
        items.forEachIndexed { index, itemStack ->
            if (!itemStack.isEmpty) {
                container.setItem(index, itemStack)
            }
        }
        return ContainerWrapper(container)
    }
}

@WithMinecraft
internal class VerificationStorageTests : StorageTests() {
    override fun createStorage(items: List<ItemStack>, secondary: Boolean): AccessibleAgnosticItemStorage {
        if (secondary) {
            val container = SimpleContainer(items.size)
            items.forEachIndexed { index, itemStack ->
                if (!itemStack.isEmpty) {
                    container.setItem(index, itemStack)
                }
            }
            return ContainerWrapper(container)
        }
        return DummyStorage(items.size, items)
    }
}

@WithMinecraft
internal class ReverseVerificationStorageTests : StorageTests() {
    override fun createStorage(items: List<ItemStack>, secondary: Boolean): AccessibleAgnosticItemStorage {
        if (!secondary) {
            val container = SimpleContainer(items.size)
            items.forEachIndexed { index, itemStack ->
                if (!itemStack.isEmpty) {
                    container.setItem(index, itemStack)
                }
            }
            return ContainerWrapper(container)
        }
        return DummyStorage(items.size, items)
    }
}
