package site.siredvin.broccolium.modules.base.util

import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

object TextBookUtils {
    private const val BOOK_MAX_LINES = 14
    private const val MAX_WIDTH_PER_LINE = 95

    fun getBookText(book: ItemStack): List<String> {
        return when (book.item) {
            Items.WRITABLE_BOOK -> {
                val content = book.components.get(DataComponents.WRITABLE_BOOK_CONTENT) ?: return emptyList()
                return content.getPages(false).toList()
            }
            Items.WRITTEN_BOOK -> {
                val content = book.components.get(DataComponents.WRITTEN_BOOK_CONTENT) ?: return emptyList()
                return content.getPages(false).map { it.string }
            }
            else -> emptyList()
        }
    }

    fun getCharacterWidth(c: Char): Int = when (c) {
        ' ', '!', '\'', ',', '.', ':', ';', 'i', '|' -> 1
        '`', 'l' -> 2
        '"', '(', ')', '*', 'I', '[', ']', 't', '{', '}' -> 3
        '<', '>', 'f', 'k' -> 4
        '@', '~' -> 6
        else -> 5
    }

    fun stripText(text: String): String {
        var lineCounter = 0
        var currentWidth = 0
        var skipNext = false
        val buffer = StringBuffer()
        for (c in text) {
            if (skipNext) {
                skipNext = false
                continue
            }
            when (c) {
                '\n' -> {
                    currentWidth = 0
                    lineCounter++
                    if (lineCounter > BOOK_MAX_LINES) {
                        return buffer.toString()
                    }
                }
                '§' -> skipNext = true
                else -> {
                    val nextWidth = getCharacterWidth(c)
                    if (currentWidth + nextWidth > MAX_WIDTH_PER_LINE) {
                        lineCounter++
                        currentWidth = nextWidth
                        if (lineCounter > BOOK_MAX_LINES) {
                            return buffer.toString()
                        }
                    } else {
                        currentWidth += nextWidth
                    }
                }
            }
            buffer.append(c)
        }
        return buffer.toString()
    }
}
