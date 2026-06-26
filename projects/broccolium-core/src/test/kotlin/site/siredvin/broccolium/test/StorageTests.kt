package site.siredvin.broccolium.test

import net.minecraft.world.SimpleContainer
import net.minecraft.world.item.ItemStack
import site.siredvin.broccolium.modules.storage.base.api.AccessibleAgnosticStorage
import site.siredvin.broccolium.modules.storage.base.api.SlottedAgnosticStorage
import site.siredvin.broccolium.modules.storage.item.ContainerWrapper
import site.siredvin.broccolium.test.storage.DummyStorage
import site.siredvin.broccolium.test.storage.NoTakebackDummyStorage

@WithMinecraft
internal class BaseStorageTests : StorageTests() {
    override fun createStorage(items: List<ItemStack>, secondary: Boolean): AccessibleAgnosticStorage<ItemStack, Int> = DummyStorage(items.size, items)
}

@WithMinecraft
internal class NoTakebackStorageTests : StorageTests() {
    override fun createStorage(items: List<ItemStack>, secondary: Boolean): AccessibleAgnosticStorage<ItemStack, Int> {
        if (secondary) {
            return DummyStorage(items.size, items)
        }
        return NoTakebackDummyStorage(items.size, items)
    }
}

@WithMinecraft
internal class BaseSlottedStorageTests : SlottedStorageTests() {
    override fun createSlottedStorage(items: List<ItemStack>, secondary: Boolean): SlottedAgnosticStorage<ItemStack, Int> {
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
    override fun createStorage(items: List<ItemStack>, secondary: Boolean): AccessibleAgnosticStorage<ItemStack, Int> {
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
    override fun createStorage(items: List<ItemStack>, secondary: Boolean): AccessibleAgnosticStorage<ItemStack, Int> {
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
