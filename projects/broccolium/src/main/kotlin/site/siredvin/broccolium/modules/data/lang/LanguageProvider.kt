package site.siredvin.broccolium.modules.data.lang

import com.google.gson.JsonObject
import net.minecraft.data.CachedOutput
import net.minecraft.data.DataProvider
import net.minecraft.data.PackOutput
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import site.siredvin.broccolium.modules.data.api.ModInformationHolder
import site.siredvin.broccolium.modules.data.api.TextRecord
import java.util.concurrent.CompletableFuture
import java.util.stream.Stream

abstract class LanguageProvider(
    protected val output: PackOutput,
    protected val modID: String,
    protected val locale: String,
    protected open val informationHolder: ModInformationHolder,
    protected vararg val textRecords: TextRecord,
) : DataProvider {
    protected val translations: MutableMap<String, String> = mutableMapOf()
    override fun run(cachedOutput: CachedOutput): CompletableFuture<*> {
        addTranslations()
        getExpectedKeys().forEach { x -> check(translations.containsKey(x)) { "No translation for $x" } }

        val json = JsonObject()
        for ((key, value) in translations) {
            json.addProperty(
                key,
                value,
            )
        }
        return DataProvider.saveStable(
            cachedOutput,
            json,
            output.outputFolder.resolve("assets/$modID/lang/$locale.json"),
        )
    }

    open fun getExpectedKeys(): Stream<String> = Stream.of(
        informationHolder.blocks.stream().map { it.get().descriptionId },
        informationHolder.items.stream().map { it.get().descriptionId },
        informationHolder.customStats.stream().map { it.get().value.toStatTranslationKey() },
        textRecords.map { it.textID }.stream(),
    ).flatMap { it }

    abstract fun addTranslations()
    fun add(id: String, text: String) {
        require(!translations.containsKey(id)) { "Duplicate translation $id" }
        translations[id] = text
    }

    fun add(item: Item, text: String, tooltip: String? = null) {
        add(item.descriptionId, text)
        if (tooltip != null) {
            add(item.descriptionId + ".tooltip", tooltip)
        }
    }

    fun add(block: Block, text: String, tooltip: String? = null) {
        add(block.descriptionId, text)
        if (tooltip != null) {
            add(block.descriptionId + ".tooltip", tooltip)
        }
    }

    fun add(record: TextRecord, text: String) {
        add(record.textID, text)
    }

    override fun getName(): String = "Language$locale"
}
