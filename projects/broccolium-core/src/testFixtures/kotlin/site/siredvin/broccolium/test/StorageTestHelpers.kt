package site.siredvin.broccolium.test

import site.siredvin.broccolium.modules.storage.base.api.AgnosticStorage
import site.siredvin.broccolium.modules.storage.base.api.SlottedAgnosticStorage
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

object StorageTestHelpers {
    fun <T, L : Number> assertNoOverlap(vararg storages: AgnosticStorage<T, L>) {
        val stacks = Collections.newSetFromMap(IdentityHashMap<T, Boolean>())
        for (storage in storages) {
            storage.getContent().forEach {
                if (!storage.operator.isEmpty(it)) {
                    if (!stacks.add(it)) {
                        throw AssertionError("Duplicate item in inventories")
                    }
                }
            }
        }
    }

    fun <T, L : Number> assertStorage(storage: AgnosticStorage<T, L>, expected: List<L>, name: String) {
        val notFoundExpected = expected.filter { !storage.operator.isZero(it) }.toMutableList()
        storage.getContent().forEach {
            if (!storage.operator.isEmpty(it)) {
                assertTrue(notFoundExpected.remove(storage.operator.getSize(it)), "In $name storage found stack with unexpected count ${storage.operator.getSize(it)}")
            }
        }
        assertTrue(notFoundExpected.isEmpty(), "Cannot find stack with this sizes: $notFoundExpected in $name storage")
    }

    fun <T, L : Number> assertSlottedStorage(storage: SlottedAgnosticStorage<T, L>, expected: List<L>, name: String) {
        expected.forEachIndexed { index, amount ->
            assertEquals(
                amount,
                storage.operator.getSize(storage.get(index)),
                "Item in slot $index for $name storage, has incorrect amount",
            )
        }
    }
}
