package site.siredvin.broccolium.test

import net.minecraft.world.item.ItemStack
import site.siredvin.broccolium.modules.storage.fluid.AgnosticFluidStack
import site.siredvin.broccolium.modules.storage.fluid.api.AgnosticFluidStorage
import site.siredvin.broccolium.modules.storage.item.api.AgnosticItemStorage
import site.siredvin.broccolium.modules.storage.item.api.SlottedAgnosticItemStorage
import java.util.*
import kotlin.test.junit5.JUnit5Asserter.assertEquals
import kotlin.test.junit5.JUnit5Asserter.assertTrue

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
                assertTrue("In $name storage found stack with unexpected count ${it.count}", notFoundExpected.remove(it.count))
            }
        }
        assertTrue("Cannot find stack with this sizes: $notFoundExpected in $name storage", notFoundExpected.isEmpty())
    }

    fun assertFluidStorage(storage: AgnosticFluidStorage, expected: List<Double>, name: String) {
        val notFoundExpected = expected.toMutableList()
        storage.getFluids().forEach {
            if (!it.isEmpty) {
                assertTrue("In $name storage found stack with unexpected count ${it.amount}", notFoundExpected.remove(it.amount))
            }
        }
        assertTrue("Cannot find stack with this sizes: $notFoundExpected in $name storage", notFoundExpected.isEmpty())
    }

    fun assertSlottedStorage(storage: SlottedAgnosticItemStorage, expected: List<Int>, name: String) {
        expected.forEachIndexed { index, amount ->
            assertEquals(
                "Item in slot $index for $name storage, has incorrect amount",
                amount,
                storage.getItem(index).count,
            )
        }
    }
}
