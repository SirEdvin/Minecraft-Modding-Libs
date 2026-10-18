package site.siredvin.broccolium.test

import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.TooltipDisplay
import net.minecraft.world.level.block.Blocks
import org.junit.jupiter.api.Test
import site.siredvin.broccolium.modules.base.item.DescriptiveBlockItem
import site.siredvin.broccolium.modules.base.item.DescriptiveItem
import site.siredvin.broccolium.modules.base.item.HiddenDescriptiveBlockItem
import site.siredvin.broccolium.modules.base.item.HiddenDescriptiveItemItem
import java.util.function.Function
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@WithMinecraft
class TargetTooltipTests {
    private fun properties(name: String) = Item.Properties().setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("interaction_test", name)))

    private fun tooltip(item: Item): List<String> {
        val lines = mutableListOf<Component>()
        item.appendHoverText(ItemStack(item), Item.TooltipContext.EMPTY, TooltipDisplay.DEFAULT, lines::add, TooltipFlag.NORMAL)
        return lines.map { it.string }
    }

    @Test
    fun absentTranslationsDoNotLeakRawKeysForItemsOrBlocks() {
        assertEquals(emptyList(), tooltip(DescriptiveItem(properties("untranslated_item"))))
        assertEquals(emptyList(), tooltip(DescriptiveBlockItem(Blocks.STONE, properties("untranslated_block"))))
    }

    @Test
    fun alwaysShownItemHooksKeepOrderAndAreComputedOnceWhileEnableSupplierIsLive() {
        var calls = 0
        var enabled = true
        val item = HiddenDescriptiveItemItem(
            properties("hidden_item"),
            { enabled },
            true,
            Function { calls++; listOf(Component.literal("first"), Component.literal("second")) },
            Function { calls++; listOf(Component.literal("third")) },
        )
        repeat(2) { assertEquals(listOf("first", "second", "third"), tooltip(item)) }
        assertEquals(2, calls)
        assertTrue(item.isEnabled())
        enabled = false
        assertFalse(item.isEnabled())
    }

    @Test
    fun alwaysShownBlockHooksKeepOrderAndAreComputedOnceWithoutClientInput() {
        var calls = 0
        val item = HiddenDescriptiveBlockItem(
            Blocks.STONE,
            properties("hidden_block"),
            { true },
            true,
            Function { calls++; listOf(Component.literal("block first"), Component.literal("block second")) },
        )
        repeat(2) { assertEquals(listOf("block first", "block second"), tooltip(item)) }
        assertEquals(1, calls)
        assertTrue(item.isEnabled())
    }
}
