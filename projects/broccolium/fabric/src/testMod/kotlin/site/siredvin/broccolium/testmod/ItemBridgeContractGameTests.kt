package site.siredvin.broccolium.testmod

//? if >=26.1 {
/*import site.siredvin.broccolium.testmod.NativeTestStructures as FabricGameTest
*///?} else {
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest
//?}
//? if >=26.1 {
/*import net.fabricmc.fabric.api.transfer.v1.item.ContainerStorage as InventoryStorage
*///?} else {
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage
//?}
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction
//? if >=26.1 {
/*import site.siredvin.testiarium.api.GameTest
*///?} else {
import net.minecraft.gametest.framework.GameTest
//?}
import net.minecraft.gametest.framework.GameTestHelper
import net.minecraft.world.SimpleContainer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import site.siredvin.broccolium.modules.storage.FabricStorageUtils
import site.siredvin.broccolium.modules.storage.base.api.SlottedAgnosticStorage
import site.siredvin.broccolium.modules.storage.item.ContainerWrapper
import site.siredvin.broccolium.modules.storage.item.FabricSlottedStorageWrapper
import site.siredvin.broccolium.modules.storage.item.ReplaceableItemStorage
import site.siredvin.broccolium.modules.storage.item.SlottedAgnosticItemStorageWrapper
import site.siredvin.testiarium.api.TestGroup
import site.siredvin.testiarium.api.immediate

@TestGroup("broccolium_reverse")
class ItemBridgeContractGameTests {
    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    fun emptySlotInsertionCommitAndAbort(helper: GameTestHelper) = helper.immediate {
        reverseFixture { container, _, original, sentinel ->
            val agnostic = ContainerWrapper(container)
            try {
                val empty = FabricStorageUtils.getSlot(agnostic, 2)
                Transaction.openOuter().use {
                    check(empty.insert(ItemVariant.of(original), 4, it) == 4L)
                    assertStack(container.getItem(2), original.copyWithCount(4), "uncommitted insertion")
                    it.abort()
                }
                assertContents(container, original, sentinel)
                Transaction.openOuter().use {
                    check(empty.insert(ItemVariant.of(original), 4, it) == 4L)
                    it.commit()
                }
                assertStack(container.getItem(0), original, "unrelated original")
                assertStack(container.getItem(1), sentinel, "unrelated sentinel")
                assertStack(container.getItem(2), original.copyWithCount(4), "committed insertion")
                check((0 until container.containerSize).sumOf { container.getItem(it).count } == 21)
            } finally {
                SlottedAgnosticItemStorageWrapper.WRAPPERS.remove(agnostic)
            }
        }
    }

    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    fun fullExtractionCommitAndAbort(helper: GameTestHelper) = helper.immediate {
        reverseFixture { container, slot, original, sentinel ->
            Transaction.openOuter().use {
                check(slot.extract(ItemVariant.of(original), 100, it) == 10L)
                assertContents(container, ItemStack.EMPTY, sentinel)
                it.abort()
            }
            assertContents(container, original, sentinel)
            Transaction.openOuter().use {
                check(slot.extract(ItemVariant.of(original), 100, it) == 10L)
                it.commit()
            }
            assertContents(container, ItemStack.EMPTY, sentinel)
        }
    }

    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    fun variantReplacementCommitAndNestedAbort(helper: GameTestHelper) = helper.immediate {
        reverseFixture { container, slot, original, sentinel ->
            Transaction.openOuter().use { outer ->
                check(slot.extract(ItemVariant.of(original), 10, outer) == 10L)
                check(slot.insert(ItemVariant.of(sentinel), 4, outer) == 4L)
                assertContents(container, sentinel.copyWithCount(4), sentinel)
                outer.openNested().use { inner ->
                    check(slot.extract(ItemVariant.of(sentinel), 4, inner) == 4L)
                    check(slot.insert(ItemVariant.of(Items.EMERALD), 6, inner) == 6L)
                    inner.commit()
                }
                assertContents(container, ItemStack(Items.EMERALD, 6), sentinel)
                outer.abort()
            }
            assertContents(container, original, sentinel)
            Transaction.openOuter().use {
                check(slot.extract(ItemVariant.of(original), 10, it) == 10L)
                check(slot.insert(ItemVariant.of(sentinel), 4, it) == 4L)
                it.commit()
            }
            assertContents(container, sentinel.copyWithCount(4), sentinel)
        }
    }

    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    fun detachedGetterSupportsCommitAndRollback(helper: GameTestHelper) = helper.immediate {
        reverseFixture({ backing ->
            object : ReplaceableItemStorage by backing {
                override fun get(slot: Int): ItemStack = backing.get(slot).copy()
            }
        }) { container, slot, original, sentinel ->
            Transaction.openOuter().use { outer ->
                check(slot.insert(ItemVariant.of(original), 2, outer) == 2L)
                outer.openNested().use { inner ->
                    check(slot.extract(ItemVariant.of(original), 12, inner) == 12L)
                    check(slot.insert(ItemVariant.of(sentinel), 4, inner) == 4L)
                    inner.commit()
                }
                assertContents(container, sentinel.copyWithCount(4), sentinel)
                outer.abort()
            }
            assertContents(container, original, sentinel)
            Transaction.openOuter().use {
                check(slot.extract(ItemVariant.of(original), 3, it) == 3L)
                it.commit()
            }
            assertContents(container, original.copyWithCount(7), sentinel)
        }
    }

    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    fun rollbackBypassesInsertionPolicy(helper: GameTestHelper) = helper.immediate {
        reverseFixture({ backing ->
            object : ReplaceableItemStorage by backing {
                override fun canPlace(slot: Int, item: ItemStack): Boolean = false
            }
        }) { container, slot, original, sentinel ->
            Transaction.openOuter().use {
                check(slot.insert(ItemVariant.of(original), 3, it) == 0L)
                check(container.changes == 0)
                check(slot.extract(ItemVariant.of(original), 10, it) == 10L)
                assertContents(container, ItemStack.EMPTY, sentinel)
                it.abort()
            }
            assertContents(container, original, sentinel)
        }
    }

    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    fun replacementCopiesCallerStack(helper: GameTestHelper) = helper.immediate {
        reverseFixture { container, _, original, sentinel ->
            val backing = ContainerWrapper(container)
            val replacement = sentinel.copyWithCount(4)
            backing.replaceSlot(0, replacement)
            replacement.count = 1
            assertContents(container, sentinel.copyWithCount(4), sentinel)
            backing.replaceSlot(0, ItemStack.EMPTY)
            assertContents(container, ItemStack.EMPTY, sentinel)
            backing.replaceSlot(0, original)
            assertContents(container, original, sentinel)
        }
    }

    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    fun insertionHonorsPerSlotLimit(helper: GameTestHelper) = helper.immediate {
        reverseFixture({ backing ->
            object : ReplaceableItemStorage by backing {
                override fun getLimit(slot: Int): Long = 12
            }
        }) { container, slot, original, sentinel ->
            check(slot.capacity == 12L)
            Transaction.openOuter().use {
                check(slot.insert(ItemVariant.of(original), 50, it) == 2L)
                it.commit()
            }
            assertContents(container, original.copyWithCount(12), sentinel)
        }
    }

    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    fun unsupportedReverseAdapterRefusesBeforeWrites(helper: GameTestHelper) = helper.immediate {
        reverseFixture { container, _, original, sentinel ->
            for (detached in listOf(false, true)) {
                val backing = ContainerWrapper(container)
                // Hide the opt-in contract: additive store and a live getter are not sufficient.
                val unsupported = object : SlottedAgnosticStorage<ItemStack, Int> by backing {
                    override fun get(slot: Int): ItemStack = if (detached) backing.get(slot).copy() else backing.get(slot)
                }
                val source = SimpleContainer(original.copyWithCount(5))
                val nativeSource = InventoryStorage.of(source, null).getSlot(0)
                expectUnsupported { SlottedAgnosticItemStorageWrapper(unsupported) }
                expectUnsupported { SlottedAgnosticItemStorageWrapper.of(unsupported) }
                Transaction.openOuter().use { transaction ->
                    expectUnsupported {
                        val destination = FabricStorageUtils.getSlot(unsupported, 0)
                        val variant = ItemVariant.of(original)
                        val amount = nativeSource.extract(variant, 3, transaction)
                        destination.insert(variant, amount, transaction)
                    }
                    // Committing after refusal must still conserve both inventories.
                    transaction.commit()
                }
                check(!SlottedAgnosticItemStorageWrapper.WRAPPERS.containsKey(unsupported))
                check(container.changes == 0) { "Unsupported storage was mutated" }
                assertContents(container, original, sentinel)
                assertStack(source.getItem(0), original.copyWithCount(5), "refused transfer source")
            }
        }
    }

    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    fun oversizedStacksRefusedWithoutTruncation(helper: GameTestHelper) = helper.immediate {
        reverseFixture { container, slot, original, sentinel ->
            val backing = ContainerWrapper(container)
            val oversized = original.copyWithCount(minOf(backing.maxStackSize, original.maxStackSize) + 1)
            try {
                backing.replaceSlot(0, oversized)
                error("Oversized replacement must be refused before the vanilla setter clamps it")
            } catch (expected: IllegalArgumentException) {
                check(expected.message?.contains("representable") == true)
            }
            assertContents(container, original, sentinel)
            check(container.changes == 0)
            // Model an already-invalid inventory (for example one loaded from external data).
            container.getItem(0).count = oversized.count
            Transaction.openOuter().use {
                expectUnsupported { slot.extract(ItemVariant.of(original), 3, it) }
                it.commit()
            }
            assertContents(container, oversized, sentinel)
            check(container.changes == 0)
        }
    }

    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    fun nativeSlotCommitAbortAndNestedRollback(helper: GameTestHelper) = helper.immediate {
        reverseFixture { container, _, original, sentinel ->
            val native = FabricSlottedStorageWrapper(InventoryStorage.of(container, null))
            val slot = FabricStorageUtils.getSlot(native, 0)
            check(slot === native.getSingleSlot(0))
            Transaction.openOuter().use { outer ->
                check(slot.extract(ItemVariant.of(original), 10, outer) == 10L)
                outer.openNested().use { inner ->
                    check(slot.insert(ItemVariant.of(sentinel), 4, inner) == 4L)
                    inner.commit()
                }
                assertContents(container, sentinel.copyWithCount(4), sentinel)
                outer.abort()
            }
            assertContents(container, original, sentinel)
            Transaction.openOuter().use {
                check(slot.insert(ItemVariant.of(original), 3, it) == 3L)
                it.commit()
            }
            assertContents(container, original.copyWithCount(13), sentinel)
            Transaction.openOuter().use {
                check(slot.extract(ItemVariant.of(original), 13, it) == 13L)
                it.abort()
            }
            assertContents(container, original.copyWithCount(13), sentinel)
            check(!SlottedAgnosticItemStorageWrapper.WRAPPERS.containsKey(native))
        }
    }

    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    fun nestedAbortPreservesOuterCommit(helper: GameTestHelper) = helper.immediate {
        reverseFixture { container, slot, original, sentinel ->
            Transaction.openOuter().use { outer ->
                check(slot.insert(ItemVariant.of(original), 2, outer) == 2L)
                outer.openNested().use { inner ->
                    check(slot.extract(ItemVariant.of(original), 12, inner) == 12L)
                    check(slot.insert(ItemVariant.of(sentinel), 4, inner) == 4L)
                    assertContents(container, sentinel.copyWithCount(4), sentinel)
                    inner.abort()
                }
                assertContents(container, original.copyWithCount(12), sentinel)
                outer.commit()
            }
            assertContents(container, original.copyWithCount(12), sentinel)
        }
    }
}

private fun expectUnsupported(action: () -> Unit) {
    try {
        action()
    } catch (expected: UnsupportedOperationException) {
        check(expected.message?.contains("ReplaceableItemStorage") == true)
        return
    }
    error("Unsupported reverse adapter must be refused")
}
