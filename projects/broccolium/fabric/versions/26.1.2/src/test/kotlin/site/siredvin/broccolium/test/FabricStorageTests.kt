package site.siredvin.broccolium.test

import net.fabricmc.fabric.api.transfer.v1.item.ContainerStorage
import net.minecraft.world.SimpleContainer
import net.minecraft.world.item.ItemStack
import org.junit.jupiter.api.extension.ExtendWith
import site.siredvin.broccolium.modules.storage.base.api.AccessibleAgnosticStorage
import site.siredvin.broccolium.modules.storage.base.api.SlottedAgnosticStorage
import site.siredvin.broccolium.modules.storage.item.ContainerWrapper
import site.siredvin.broccolium.modules.storage.item.FabricSlottedStorageWrapper
import site.siredvin.broccolium.modules.storage.item.FabricStorageWrapper
import site.siredvin.broccolium.test.storage.DummyStorage

internal class TweakedFabricStorageWrapper(private val inventoryStorage: ContainerStorage) : FabricStorageWrapper(inventoryStorage), AccessibleAgnosticStorage<ItemStack, Int> {
    override fun get(slot: Int): ItemStack {
        val view = inventoryStorage.getSlot(slot)
        return view.resource.toStack(view.amount.toInt())
    }
}

private fun container(items: List<ItemStack>): SimpleContainer = SimpleContainer(items.size).also { container ->
    items.forEachIndexed { index, stack -> if (!stack.isEmpty) container.setItem(index, stack.copy()) }
}

@WithMinecraft
@ExtendWith(BroccoliumInitialization::class)
internal class FabricSlottedStorageTests : SlottedStorageTests() {
    override fun createSlottedStorage(items: List<ItemStack>, secondary: Boolean): SlottedAgnosticStorage<ItemStack, Int> = FabricSlottedStorageWrapper(ContainerStorage.of(container(items), null))
}

@WithMinecraft
@ExtendWith(BroccoliumInitialization::class)
internal class FabricStorageTests : StorageTests() {
    override fun createStorage(items: List<ItemStack>, secondary: Boolean): AccessibleAgnosticStorage<ItemStack, Int> = TweakedFabricStorageWrapper(ContainerStorage.of(container(items), null))
}

@WithMinecraft
@ExtendWith(BroccoliumInitialization::class)
internal class CompactFabricSlottedStorageTests : SlottedStorageTests() {
    override fun createSlottedStorage(items: List<ItemStack>, secondary: Boolean): SlottedAgnosticStorage<ItemStack, Int> = if (secondary) ContainerWrapper(container(items)) else FabricSlottedStorageWrapper(ContainerStorage.of(container(items), null))
}

@WithMinecraft
@ExtendWith(BroccoliumInitialization::class)
internal class CompactFabricStorageTests : StorageTests() {
    override fun createStorage(items: List<ItemStack>, secondary: Boolean): AccessibleAgnosticStorage<ItemStack, Int> = if (secondary) DummyStorage(items.size, items) else TweakedFabricStorageWrapper(ContainerStorage.of(container(items), null))
}

@WithMinecraft
@ExtendWith(BroccoliumInitialization::class)
internal class VerificationFabricStorageTests : StorageTests() {
    override fun createStorage(items: List<ItemStack>, secondary: Boolean): AccessibleAgnosticStorage<ItemStack, Int> = if (secondary) FabricSlottedStorageWrapper(ContainerStorage.of(container(items), null)) else TweakedFabricStorageWrapper(ContainerStorage.of(container(items), null))
}

@WithMinecraft
@ExtendWith(BroccoliumInitialization::class)
internal class ReverseVerificationFabricStorageTests : StorageTests() {
    override fun createStorage(items: List<ItemStack>, secondary: Boolean): AccessibleAgnosticStorage<ItemStack, Int> = if (!secondary) FabricSlottedStorageWrapper(ContainerStorage.of(container(items), null)) else TweakedFabricStorageWrapper(ContainerStorage.of(container(items), null))
}
