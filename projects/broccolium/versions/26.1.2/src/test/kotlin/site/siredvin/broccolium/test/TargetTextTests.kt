package site.siredvin.broccolium.test

import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.contents.TranslatableContents
import net.minecraft.resources.Identifier
import net.minecraft.server.network.Filterable
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.WritableBookContent
import net.minecraft.world.item.component.WrittenBookContent
import org.junit.jupiter.api.Test
import site.siredvin.broccolium.modules.base.util.TextBookUtils
import site.siredvin.broccolium.modules.base.util.itemExtra
import site.siredvin.broccolium.modules.base.util.itemTooltip
import site.siredvin.broccolium.modules.base.util.pocketAdjectiveComponent
import site.siredvin.broccolium.modules.base.util.turtleAdjectiveComponent
import java.util.Optional
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@WithMinecraft
class TargetTextTests {
    @Test
    fun writtenBookExtractionPreservesRawStyledUnicodePagesWithoutMutatingComponents() {
        val first = Component.literal("Перша 🌳").append(Component.literal(" page").withStyle(net.minecraft.ChatFormatting.BOLD))
        val content = WrittenBookContent(
            Filterable("raw title", Optional.of("filtered title")),
            "author",
            2,
            listOf(Filterable(first, Optional.of(Component.literal("filtered"))), Filterable.passThrough(Component.empty()), Filterable.passThrough(Component.literal("third\nline"))),
            true,
        )
        val book = ItemStack(Items.WRITTEN_BOOK)
        book.set(DataComponents.WRITTEN_BOOK_CONTENT, content)
        val before = book.copy()

        repeat(2) { assertEquals(listOf("Перша 🌳 page", "", "third\nline"), TextBookUtils.getBookText(book)) }
        assertTrue(ItemStack.matches(before, book))
        assertEquals(content, book.get(DataComponents.WRITTEN_BOOK_CONTENT))
    }

    @Test
    fun writableBookExtractionKeepsEmptyPagesAndDoesNotTreatForeignComponentsAsBooks() {
        val content = WritableBookContent(listOf(Filterable.passThrough(""), Filterable("raw\nline", Optional.of("filtered")), Filterable.passThrough("останній")))
        val book = ItemStack(Items.WRITABLE_BOOK)
        book.set(DataComponents.WRITABLE_BOOK_CONTENT, content)
        val before = book.copy()
        assertEquals(listOf("", "raw\nline", "останній"), TextBookUtils.getBookText(book))
        assertTrue(ItemStack.matches(before, book))
        val stone = ItemStack(Items.STONE)
        stone.set(DataComponents.WRITABLE_BOOK_CONTENT, content)
        assertEquals(emptyList(), TextBookUtils.getBookText(stone))
        book.remove(DataComponents.WRITABLE_BOOK_CONTENT)
        assertEquals(emptyList(), TextBookUtils.getBookText(book))
    }

    @Test
    fun booksUseRawNativeComponentsAndKeepPageOrder() {
        val writable = ItemStack(Items.WRITABLE_BOOK)
        writable.set(DataComponents.WRITABLE_BOOK_CONTENT, WritableBookContent(listOf(Filterable("raw", Optional.of("filtered")), Filterable.passThrough("second"))))
        assertEquals(listOf("raw", "second"), TextBookUtils.getBookText(writable))
        val written = ItemStack(Items.WRITTEN_BOOK)
        written.set(DataComponents.WRITTEN_BOOK_CONTENT, WrittenBookContent(Filterable.passThrough("title"), "author", 0, listOf(Filterable.passThrough(Component.literal("one")), Filterable.passThrough(Component.literal("two"))), true))
        assertEquals(listOf("one", "two"), TextBookUtils.getBookText(written))
        assertEquals(emptyList(), TextBookUtils.getBookText(ItemStack(Items.STONE)))
        written.remove(DataComponents.WRITTEN_BOOK_CONTENT)
        assertEquals(emptyList(), TextBookUtils.getBookText(written))
    }

    @Test
    fun translationKeysArgumentsAndAlwaysShownTooltipHooksStayStable() {
        assertEquals("item.example.fixture.tooltip", (itemTooltip("item.example.fixture").contents as TranslatableContents).key)
        val extra = itemExtra("item.example.fixture", "range", 12).contents as TranslatableContents
        assertEquals("item.example.fixture.extra.range", extra.key)
        assertEquals(listOf(12), extra.args.toList())
        val id = Identifier.fromNamespaceAndPath("example", "fixture")
        assertEquals("turtle.example.fixture", (turtleAdjectiveComponent(id).contents as TranslatableContents).key)
        assertEquals("pocket.example.fixture", (pocketAdjectiveComponent(id).contents as TranslatableContents).key)
    }
}
