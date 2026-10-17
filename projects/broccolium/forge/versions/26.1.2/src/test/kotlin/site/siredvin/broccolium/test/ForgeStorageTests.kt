package site.siredvin.broccolium.test

import net.minecraft.world.SimpleContainer
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.transfer.item.VanillaContainerWrapper
import site.siredvin.broccolium.modules.storage.base.api.SlottedAgnosticStorage
import site.siredvin.broccolium.modules.storage.item.AgnosticItemHandlerWrapper
import site.siredvin.broccolium.modules.storage.item.ContainerWrapper

@WithNeoForgeRegistries
class ForgeSlottedStorageTests : SlottedStorageTests() {
    override fun createSlottedStorage(items: List<ItemStack>, secondary: Boolean): SlottedAgnosticStorage<ItemStack, Int> {
        val container = SimpleContainer(items.size)
        items.forEachIndexed { index, stack -> container.setItem(index, stack.copy()) }
        return AgnosticItemHandlerWrapper(VanillaContainerWrapper.of(container))
    }
}

@WithNeoForgeRegistries
class CompactForgeSlottedStorageTests : SlottedStorageTests() {
    override fun createSlottedStorage(items: List<ItemStack>, secondary: Boolean): SlottedAgnosticStorage<ItemStack, Int> {
        val container = SimpleContainer(items.size)
        items.forEachIndexed { index, stack -> container.setItem(index, stack.copy()) }
        return if (secondary) ContainerWrapper(container) else AgnosticItemHandlerWrapper(VanillaContainerWrapper.of(container))
    }
}
