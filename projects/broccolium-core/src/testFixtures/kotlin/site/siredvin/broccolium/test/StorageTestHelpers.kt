package site.siredvin.broccolium.test

import net.minecraft.world.item.ItemStack
import site.siredvin.broccolium.modules.storage.fluid.AgnosticFluidStack
import site.siredvin.broccolium.modules.storage.fluid.api.AgnosticFluidStorage
import site.siredvin.broccolium.modules.storage.item.api.AgnosticItemStorage
import site.siredvin.broccolium.modules.storage.item.api.SlottedAgnosticItemStorage
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

object StorageTestHelpers {
    fun assertNoOverlap(vararg storages: AgnosticItemStorage) {
        val stacks = Collections.newSetFromMap(IdentityHashMap<ItemStack, Boolean>())
        for (storage in storages) {
            storage.getItems().forEach {
                if (it != ItemStack.EMPTY) {
                    if (!stacks.add(it)) {
                        throw AssertionError("Duplicate item in inventories")
                    }
                }
            }
        }
    }

    fun assertNoOverlap(vararg storages: AgnosticFluidStorage) {
        val stacks = Collections.newSetFromMap(IdentityHashMap<AgnosticFluidStack, Boolean>())
        for (storage in storages) {
            storage.getFluids().forEach {
                if (it != AgnosticFluidStack.EMPTY) {
                    if (!stacks.add(it)) {
                        throw AssertionError("Duplicate item in inventories")
                    }
                }
            }
        }
    }

    fun assertStorage(storage: AgnosticItemStorage, expected: List<Int>, name: String) {
        val notFoundExpected = expected.toMutableList()
        storage.getItems().forEach {
            if (!it.isEmpty) {
                assertTrue(notFoundExpected.remove(it.count), "In $name storage found stack with unexpected count ${it.count}")
            }
        }
        assertTrue(notFoundExpected.isEmpty(), "Cannot find stack with this sizes: $notFoundExpected in $name storage")
    }

    fun assertFluidStorage(storage: AgnosticFluidStorage, expected: List<Long>, name: String) {
        val notFoundExpected = expected.toMutableList()
        storage.getFluids().forEach {
            if (!it.isEmpty) {
                assertTrue(notFoundExpected.remove(it.amount), "In $name storage found stack with unexpected count ${it.amount}")
            }
        }
        assertTrue(notFoundExpected.isEmpty(), "Cannot find stack with this sizes: $notFoundExpected in $name storage")
    }

    fun assertSlottedStorage(storage: SlottedAgnosticItemStorage, expected: List<Int>, name: String) {
        expected.forEachIndexed { index, amount ->
            assertEquals(
                amount,
                storage.getItem(index).count,
                "Item in slot $index for $name storage, has incorrect amount",
            )
        }
    }
}
