package site.siredvin.tweakium

import net.minecraft.core.component.DataComponents
import net.minecraft.resources.Identifier
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.EquipmentSlotGroup
import net.minecraft.world.entity.ai.attributes.AttributeMap
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.ItemAttributeModifiers
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import site.siredvin.broccolium.modules.storage.base.api.SlottedAgnosticStorage
import site.siredvin.broccolium.modules.storage.item.ContainerWrapper
import site.siredvin.broccolium.test.WithMinecraft
import site.siredvin.tweakium.modules.player.FakePlayerSessionUtil
import java.util.function.Predicate

@WithMinecraft
class FakePlayerSessionTests {
    @ParameterizedTest
    @ValueSource(ints = [0, 8, 9, 15])
    fun selectedTurtleSlotIsSwappedIntoHotbarAndRestored(selected: Int) {
        val inventory = SimpleContainer(36)
        inventory.setItem(0, ItemStack(Items.STICK, 3))
        inventory.setItem(selected, ItemStack(Items.DIAMOND, 4))
        val before = (0 until inventory.containerSize).map { inventory.getItem(it).copy() }
        FakePlayerSessionUtil.swapSelected(inventory, selected)
        assertTrue(inventory.getItem(0).`is`(Items.DIAMOND))
        inventory.getItem(0).shrink(1)
        FakePlayerSessionUtil.swapSelected(inventory, selected)
        assertEquals(3, inventory.getItem(selected).count)
        for (slot in 0 until inventory.containerSize) {
            if (slot != selected) assertTrue(ItemStack.matches(before[slot], inventory.getItem(slot)))
        }
    }

    @Test
    fun invalidSelectedSlotFailsBeforeMutation() {
        val inventory = SimpleContainer(ItemStack(Items.DIAMOND, 2))
        assertThrows(IllegalArgumentException::class.java) { FakePlayerSessionUtil.swapSelected(inventory, 1) }
        assertEquals(2, inventory.getItem(0).count)
    }

    @Test
    fun storageLoanReturnsChangedItemsWithoutDuplicatingSource() {
        val source = SimpleContainer(ItemStack(Items.DIAMOND, 8), ItemStack(Items.STICK, 2))
        val storage = ContainerWrapper(source)
        val inventory = SimpleContainer(36)
        FakePlayerSessionUtil.borrowInventory(storage, inventory) { fail("No overflow expected") }
        assertTrue(source.isEmpty)
        inventory.getItem(0).shrink(3)
        FakePlayerSessionUtil.returnInventory(storage, inventory) { fail("No overflow expected") }
        assertEquals(5, source.getItem(0).count)
        assertEquals(2, source.getItem(1).count)
        assertTrue(inventory.isEmpty)
    }

    @Test
    fun partialExtractionRefusalRestoresEveryLoanedItem() {
        val source = SimpleContainer(ItemStack(Items.DIAMOND, 8))
        val backing = ContainerWrapper(source)
        val storage = object : SlottedAgnosticStorage<ItemStack, Int> by backing {
            override fun take(limit: Int, startSlot: Int, endSlot: Int, predicate: Predicate<ItemStack>, simulate: Boolean): ItemStack = backing.take(minOf(limit, 2), startSlot, endSlot, predicate, simulate)
        }
        val inventory = SimpleContainer(36)
        assertThrows(IllegalStateException::class.java) {
            FakePlayerSessionUtil.borrowInventory(storage, inventory) { fail("No overflow expected") }
        }
        assertEquals(8, source.getItem(0).count)
        assertTrue(inventory.isEmpty)
    }

    @Test
    fun insertionRefusalReturnsRemainderWithoutDiscardingIt() {
        val source = SimpleContainer(ItemStack(Items.DIAMOND, 8))
        val backing = ContainerWrapper(source)
        val inventory = SimpleContainer(36)
        FakePlayerSessionUtil.borrowInventory(backing, inventory) { fail("No overflow expected") }
        val refusing = object : SlottedAgnosticStorage<ItemStack, Int> by backing {
            override fun store(stack: ItemStack, startSlot: Int, endSlot: Int, simulate: Boolean): ItemStack = stack.copy()
        }
        val remainders = mutableListOf<ItemStack>()
        FakePlayerSessionUtil.returnInventory(refusing, inventory) { remainders.add(it.copy()) }
        assertEquals(8, remainders.single().count)
        assertTrue(source.isEmpty)
        assertTrue(inventory.isEmpty)
    }

    @Test
    fun oversizedStorageFailsBeforeSourceOrInventoryMutation() {
        val source = SimpleContainer(ItemStack(Items.DIAMOND), ItemStack(Items.STICK))
        val inventory = SimpleContainer(ItemStack(Items.GOLD_INGOT))
        assertThrows(IllegalArgumentException::class.java) {
            FakePlayerSessionUtil.borrowInventory(ContainerWrapper(source), inventory) { fail("No overflow expected") }
        }
        assertTrue(source.getItem(0).`is`(Items.DIAMOND))
        assertTrue(source.getItem(1).`is`(Items.STICK))
        assertTrue(inventory.getItem(0).`is`(Items.GOLD_INGOT))
    }

    @Test
    fun replacingToolDuringActionDoesNotLeakOriginalAttributes() {
        val attributes = AttributeMap(AttributeSupplier.builder().add(Attributes.ATTACK_DAMAGE, 1.0).build())
        val tool = tool(5.0)
        FakePlayerSessionUtil.applyToolAttributes(tool, attributes).use {
            assertEquals(6.0, attributes.getValue(Attributes.ATTACK_DAMAGE))
            tool.set(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY)
        }
        assertEquals(1.0, attributes.getValue(Attributes.ATTACK_DAMAGE))
    }

    @Test
    fun exceptionalActionRestoresPreexistingPermanentModifier() {
        val attributes = AttributeMap(AttributeSupplier.builder().add(Attributes.ATTACK_DAMAGE, 1.0).build())
        val previous = AttributeModifier(MODIFIER_ID, 2.0, AttributeModifier.Operation.ADD_VALUE)
        val instance = attributes.getInstance(Attributes.ATTACK_DAMAGE)!!
        instance.addPermanentModifier(previous)
        assertThrows(IllegalStateException::class.java) {
            FakePlayerSessionUtil.applyToolAttributes(tool(5.0), attributes).use {
                assertEquals(6.0, attributes.getValue(Attributes.ATTACK_DAMAGE))
                throw IllegalStateException("action sentinel")
            }
        }
        assertEquals(3.0, attributes.getValue(Attributes.ATTACK_DAMAGE))
        assertTrue(previous in instance.permanentModifiers)
    }

    private fun tool(amount: Double): ItemStack = ItemStack(Items.DIAMOND).apply {
        set(
            DataComponents.ATTRIBUTE_MODIFIERS,
            ItemAttributeModifiers.builder().add(
                Attributes.ATTACK_DAMAGE,
                AttributeModifier(MODIFIER_ID, amount, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND,
            ).build(),
        )
    }

    companion object {
        private val MODIFIER_ID = Identifier.fromNamespaceAndPath("tweakium", "test_tool")
    }
}
