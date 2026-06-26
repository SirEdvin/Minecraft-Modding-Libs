package site.siredvin.broccolium.test

import net.minecraft.world.SimpleContainer
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.items.wrapper.InvWrapper
import site.siredvin.broccolium.modules.storage.base.api.SlottedAgnosticStorage
import site.siredvin.broccolium.modules.storage.item.AgnosticItemHandlerWrapper
import site.siredvin.broccolium.modules.storage.item.ContainerWrapper

@WithMinecraft
class ForgeSlottedStorageTests : SlottedStorageTests() {
    override fun createSlottedStorage(items: List<ItemStack>, secondary: Boolean): SlottedAgnosticStorage<ItemStack, Int> {
        val baseContainer = SimpleContainer(items.size)
        items.forEachIndexed { index, itemStack ->
            baseContainer.setItem(index, itemStack)
        }
        return AgnosticItemHandlerWrapper(InvWrapper(baseContainer))
    }
}

@WithMinecraft
class CompactForgeSlottedStorageTests : SlottedStorageTests() {
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
        val baseContainer = SimpleContainer(items.size)
        items.forEachIndexed { index, itemStack ->
            baseContainer.setItem(index, itemStack)
        }
        return AgnosticItemHandlerWrapper(InvWrapper(baseContainer))
    }
}
