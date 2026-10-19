package site.siredvin.broccolium.testmod

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction
import net.minecraft.gametest.framework.GameTestHelper
import net.minecraft.world.SimpleContainer
import net.minecraft.world.item.ItemStack
import site.siredvin.broccolium.modules.storage.FabricStorageUtils
import site.siredvin.broccolium.modules.storage.base.api.AgnosticSink
import site.siredvin.broccolium.modules.storage.item.ContainerWrapper
import site.siredvin.broccolium.modules.storage.item.FabricSlottedStorageWrapper
import site.siredvin.broccolium.modules.storage.item.FabricStorageWrapper
import site.siredvin.broccolium.modules.storage.item.ReplaceableItemStorage
import site.siredvin.broccolium.modules.storage.item.SlottedAgnosticItemStorageWrapper
import site.siredvin.testiarium.api.TestGroup
import site.siredvin.testiarium.api.immediate

@TestGroup("broccolium_reverse")
class ReverseItemBridgeGameTests {
    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    fun occupiedInsertCommit(helper: GameTestHelper) = helper.immediate {
        reverseFixture { container, slot, original, sentinel ->
            Transaction.openOuter().use {
                check(slot.insert(ItemVariant.of(original), 3, it) == 3L)
                it.commit()
            }
            assertContents(container, original.copyWithCount(13), sentinel)
            check(container.changes > 0) { "Committed insertion must mark the container changed" }
        }
    }

    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    fun occupiedExtractCommit(helper: GameTestHelper) = helper.immediate {
        reverseFixture { container, slot, original, sentinel ->
            Transaction.openOuter().use {
                check(slot.extract(ItemVariant.of(original), 3, it) == 3L)
                it.commit()
            }
            assertContents(container, original.copyWithCount(7), sentinel)
            check(container.changes > 0) { "Committed extraction must mark the container changed" }
        }
    }

    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    fun occupiedInsertAbort(helper: GameTestHelper) = helper.immediate {
        reverseFixture { container, slot, original, sentinel ->
            Transaction.openOuter().use {
                check(slot.insert(ItemVariant.of(original), 3, it) == 3L)
                it.abort()
            }
            assertContents(container, original, sentinel)
        }
    }

    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    fun occupiedExtractAbort(helper: GameTestHelper) = helper.immediate {
        reverseFixture { container, slot, original, sentinel ->
            Transaction.openOuter().use {
                check(slot.extract(ItemVariant.of(original), 3, it) == 3L)
                it.abort()
            }
            assertContents(container, original, sentinel)
        }
    }

    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    fun nestedInsertCommitOuterAbort(helper: GameTestHelper) = helper.immediate {
        reverseFixture { container, slot, original, sentinel ->
            Transaction.openOuter().use { outer ->
                check(slot.insert(ItemVariant.of(original), 2, outer) == 2L)
                outer.openNested().use { inner ->
                    check(slot.insert(ItemVariant.of(original), 3, inner) == 3L)
                    inner.commit()
                }
                outer.abort()
            }
            assertContents(container, original, sentinel)
        }
    }

    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    fun nestedExtractCommitOuterAbort(helper: GameTestHelper) = helper.immediate {
        reverseFixture { container, slot, original, sentinel ->
            Transaction.openOuter().use { outer ->
                check(slot.extract(ItemVariant.of(original), 2, outer) == 2L)
                outer.openNested().use { inner ->
                    check(slot.extract(ItemVariant.of(original), 3, inner) == 3L)
                    inner.commit()
                }
                outer.abort()
            }
            assertContents(container, original, sentinel)
        }
    }

    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    fun differentDataCannotMergeIntoOccupiedSlot(helper: GameTestHelper) = helper.immediate {
        reverseFixture { container, slot, original, sentinel ->
            Transaction.openOuter().use {
                check(slot.insert(ItemVariant.of(sentinel), 3, it) == 0L)
                check(slot.extract(ItemVariant.of(sentinel), 3, it) == 0L)
                it.commit()
            }
            assertContents(container, original, sentinel)
            check(container.changes == 0) { "Rejected variants must not change the container" }
        }
    }
}

@TestGroup("broccolium_slots")
class ItemBridgeSlotGameTests {
    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    fun getSlotZeroPreservesCountAndData(helper: GameTestHelper) = helper.immediate {
        reverseFixture { container, slot, original, sentinel ->
            check(slot.amount == 10L && slot.resource == ItemVariant.of(original))
            val native = FabricSlottedStorageWrapper(InventoryStorage.of(container, null))
            assertStack(native.get(0), original, "native slot zero")
            val nativeSlot = FabricStorageUtils.getSlot(native, 0)
            check(nativeSlot.amount == 10L && nativeSlot.resource == ItemVariant.of(original))
            check(nativeSlot === native.getSingleSlot(0)) { "Native transactions must not be reverse-adapted" }
            assertContents(container, original, sentinel)
        }
    }

    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    fun explicitSourceZeroDoesNotSpillIntoNextSlot(helper: GameTestHelper) = helper.immediate {
        val original = bridgeStack(2, "source")
        val source = SimpleContainer(original.copy(), original.copyWithCount(7))
        val destination = SimpleContainer(2)
        val from = FabricSlottedStorageWrapper(InventoryStorage.of(source, null))
        val to = FabricStorageWrapper(InventoryStorage.of(destination, null))
        val moved = to.moveFrom(from, 5, 0) { true }
        check(moved == 2) { "Explicit source zero must move 2, not $moved from other slots" }
        assertStack(source.getItem(0), ItemStack.EMPTY, "source slot zero")
        assertStack(source.getItem(1), original.copyWithCount(7), "source sentinel")
        assertStack(destination.getItem(0), original, "destination slot zero")
        check(destination.getItem(1).isEmpty)
        check(source.getItem(1).count + destination.getItem(0).count == 9)
    }

    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    fun explicitSourceZeroDoesNotSkipRejectedVariant(helper: GameTestHelper) = helper.immediate {
        val original = bridgeStack(2, "source")
        val sentinel = bridgeStack(7, "different-data")
        val source = SimpleContainer(original.copy(), sentinel.copy())
        val destination = SimpleContainer(2)
        val from = FabricSlottedStorageWrapper(InventoryStorage.of(source, null))
        val to = FabricStorageWrapper(InventoryStorage.of(destination, null))
        val moved = to.moveFrom(from, 5, 0) { ItemVariant.of(it) == ItemVariant.of(sentinel) }
        check(moved == 0) { "Rejected source zero must not select a later matching slot: moved $moved" }
        assertStack(source.getItem(0), original, "source slot zero")
        assertStack(source.getItem(1), sentinel, "source sentinel")
        check(destination.isEmpty)
    }

    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    fun explicitDestinationZeroDoesNotSpillIntoNextSlot(helper: GameTestHelper) = helper.immediate {
        val original = bridgeStack(5, "source")
        val source = SimpleContainer(original.copy())
        val destination = SimpleContainer(original.copyWithCount(63), ItemStack.EMPTY)
        val from = FabricStorageWrapper(InventoryStorage.of(source, null))
        val moved = from.moveTo(ContainerWrapper(destination), 5, 0) { true }
        check(moved == 1) { "Explicit destination zero must accept only its remaining capacity: $moved" }
        assertStack(source.getItem(0), original.copyWithCount(4), "source")
        assertStack(destination.getItem(0), original.copyWithCount(64), "destination slot zero")
        check(destination.getItem(1).isEmpty) { "Explicit destination must not spill to slot one" }
        check(source.getItem(0).count + destination.getItem(0).count == 68)
    }

    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    fun explicitDestinationOnNonSlottedSinkUsesDocumentedFallback(helper: GameTestHelper) = helper.immediate {
        val original = bridgeStack(5, "source")
        val source = SimpleContainer(original.copy())
        val destination = SimpleContainer(2)
        // Expose the native container's real implementation only through the non-slotted contract.
        val sink = object : AgnosticSink<ItemStack, Int> by ContainerWrapper(destination) {}
        val from = FabricStorageWrapper(InventoryStorage.of(source, null))
        val moved = from.moveTo(sink, 3, 0) { true }
        check(moved == 3)
        assertStack(source.getItem(0), original.copyWithCount(2), "source")
        assertStack(destination.getItem(0), original.copyWithCount(3), "destination")
        check(destination.getItem(1).isEmpty)
        check(source.getItem(0).count + destination.getItem(0).count == 5)
    }
}

internal class ObservedContainer : SimpleContainer(3) {
    var changes = 0

    override fun setChanged() {
        super.setChanged()
        changes++
    }
}

internal fun reverseFixture(
    storageFactory: (ContainerWrapper) -> ReplaceableItemStorage = { it },
    body: (ObservedContainer, SingleSlotStorage<ItemVariant>, ItemStack, ItemStack) -> Unit,
) {
    val original = bridgeStack(10, "original-data")
    val sentinel = bridgeStack(7, "different-data")
    val container = ObservedContainer()
    container.setItem(0, original.copy())
    container.setItem(1, sentinel.copy())
    container.changes = 0
    val agnostic = storageFactory(ContainerWrapper(container))
    val wrapper = SlottedAgnosticItemStorageWrapper.of(agnostic)
    try {
        check(SlottedAgnosticItemStorageWrapper.of(agnostic) === wrapper)
        check(FabricStorageUtils.getSlot(agnostic, 0) === wrapper.getSlot(0))
        body(container, wrapper.getSlot(0), original, sentinel)
    } finally {
        SlottedAgnosticItemStorageWrapper.WRAPPERS.remove(agnostic)
    }
}

internal fun assertContents(container: SimpleContainer, expected: ItemStack, sentinel: ItemStack) {
    assertStack(container.getItem(0), expected, "occupied slot zero")
    assertStack(container.getItem(1), sentinel, "unrelated data-bearing slot")
    check(container.getItem(2).isEmpty) { "Empty slot must remain empty" }
    check((0 until container.containerSize).sumOf { container.getItem(it).count } == expected.count + sentinel.count)
}

internal fun assertStack(actual: ItemStack, expected: ItemStack, label: String) {
    check(actual.count == expected.count) { "$label: expected count ${expected.count}, got ${actual.count}" }
    check(ItemVariant.of(actual) == ItemVariant.of(expected)) { "$label: item data changed" }
}
