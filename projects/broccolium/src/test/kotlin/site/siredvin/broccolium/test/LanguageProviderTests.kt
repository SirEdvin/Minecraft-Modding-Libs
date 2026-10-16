package site.siredvin.broccolium.test

import com.google.gson.JsonParser
import net.minecraft.data.CachedOutput
import net.minecraft.data.PackOutput
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import site.siredvin.broccolium.modules.data.api.ModInformationHolder
import site.siredvin.broccolium.modules.data.lang.LanguageProvider
import java.nio.file.Path
import java.util.stream.Stream
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse

class LanguageProviderTests {
    @TempDir
    lateinit var directory: Path

    private fun provider(expected: String = "example.key", translations: LanguageProvider.() -> Unit) = object : LanguageProvider(
        PackOutput(directory),
        "example",
        "en_us",
        object : ModInformationHolder {},
    ) {
        override fun getExpectedKeys(): Stream<String> = Stream.of(expected)
        override fun addTranslations() = translations()
    }

    @Test
    fun writesEscapedUnicodeTranslationToNamespacedAsset() {
        val text = "Oak \"tree\"\nДуб & <leaves>"
        provider { add("example.key", text) }.run(CachedOutput.NO_CACHE).join()
        val json = JsonParser.parseString(directory.resolve("assets/example/lang/en_us.json").readText()).asJsonObject
        assertEquals(setOf("example.key"), json.keySet())
        assertEquals(text, json["example.key"].asString)
    }

    @Test
    fun missingRequiredKeyFailsBeforeWritingFile() {
        val error = assertFailsWith<IllegalStateException> {
            provider { add("wrong.key", "Wrong") }.run(CachedOutput.NO_CACHE)
        }
        assertEquals("No translation for example.key", error.message)
        assertFalse(directory.resolve("assets/example/lang/en_us.json").exists())
    }

    @Test
    fun duplicateTranslationIsRejectedRatherThanOverwritten() {
        val error = assertFailsWith<IllegalArgumentException> {
            provider {
                add("example.key", "first")
                add("example.key", "second")
            }.run(CachedOutput.NO_CACHE)
        }
        assertEquals("Duplicate translation example.key", error.message)
        assertFalse(directory.resolve("assets/example/lang/en_us.json").exists())
    }
}
