package site.siredvin.peripheralium.testmod

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.data.CachedOutput
import net.minecraft.data.PackOutput
import net.minecraft.gametest.framework.GameTestAssertException
import net.minecraft.gametest.framework.GameTestHelper
import net.minecraft.resources.Identifier
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import site.siredvin.peripheralium.Blocks
import site.siredvin.peripheralium.Items
import site.siredvin.peripheralium.PeripheraliumPlatform
import site.siredvin.peripheralium.data.PeripheraliumENLanguageProvider
import site.siredvin.peripheralium.data.PeripheraliumUALanguageProvider
import site.siredvin.testiarium.api.GameTest
import site.siredvin.testiarium.api.TestGroup
import site.siredvin.testiarium.api.assertCraftable
import site.siredvin.testiarium.api.immediate
import java.nio.file.Path

@TestGroup("tweakium")
class PeripheraliumBehaviorGameTests {
    @GameTest(template = "modulebehavior.empty")
    fun registeredItemsAndBlockMatchTrackedObjects(helper: GameTestHelper) = helper.immediate {
        val registered = BuiltInRegistries.ITEM.keySet().filter { it.namespace == "peripheralium" }.map { it.path }.toSet()
        check(registered == RecipeAssertions.expectedItems) { "Unexpected Peripheralium items: $registered" }
        val block = Blocks.PERIPHERALIUM_BLOCK.get()
        check(BuiltInRegistries.BLOCK.getKey(block).toString() == "peripheralium:peripheralium_block")
        check(BuiltInRegistries.ITEM.getKey(block.asItem()).toString() == "peripheralium:peripheralium_block")
        check(PeripheraliumPlatform.holder.blocks.map { it.get() } == listOf(block))
        check(PeripheraliumPlatform.holder.items.map { it.get() }.toSet() == setOf(Items.PERIPHERALIUM_DUST.get(), Items.PERIPHERALIUM_UPGRADE_TEMPLATE.get()))
        val tab = BuiltInRegistries.CREATIVE_MODE_TAB.getValue(Identifier.parse("peripheralium:tab"))!!
        tab.buildContents(CreativeModeTab.ItemDisplayParameters(helper.level.enabledFeatures(), false, helper.level.registryAccess()))
        check(tab.displayItems.map { BuiltInRegistries.ITEM.getKey(it.item).path }.toSet() == RecipeAssertions.expectedItems)
        check(tab.iconItem.item == Items.PERIPHERALIUM_DUST.get())
    }

    @GameTest(template = "modulebehavior.empty")
    fun recipeOutputsAndInvalidPatternsPreserveContracts(helper: GameTestHelper) = helper.immediate {
        RecipeAssertions.verify(helper)
    }

    @GameTest(template = "modulebehavior.empty")
    fun loadedRecipesCraftWithNativeIngredientsAndRejectWrongCenter(helper: GameTestHelper) = helper.immediate {
        val dust = Items.PERIPHERALIUM_DUST.get()
        val block = Blocks.PERIPHERALIUM_BLOCK.get().asItem()
        val template = Items.PERIPHERALIUM_UPGRADE_TEMPLATE.get()
        helper.assertCraftable(listOf(ItemStack(net.minecraft.world.item.Items.REDSTONE), ItemStack(net.minecraft.world.item.Items.GLOWSTONE_DUST)), ItemStack(dust))
        helper.assertCraftable(listOf(ItemStack(net.minecraft.world.item.Items.GLOWSTONE_DUST), ItemStack(net.minecraft.world.item.Items.REDSTONE)), ItemStack(dust))
        helper.assertCraftable(List(9) { ItemStack(dust) }, ItemStack(block))
        helper.assertCraftable(listOf(ItemStack(block)), ItemStack(dust, 9))
        val grid = MutableList(9) { ItemStack(dust) }.apply {
            this[4] = ItemStack(net.minecraft.world.item.Items.EXPERIENCE_BOTTLE)
            this[7] = ItemStack.EMPTY
        }
        helper.assertCraftable(grid, ItemStack(template, 4))
        grid[4] = ItemStack(net.minecraft.world.item.Items.DIRT)
        try {
            helper.assertCraftable(grid, ItemStack(template, 4))
            error("Wrong template ingredient must not match")
        } catch (expected: GameTestAssertException) {
            check(expected.message.startsWith("No recipe matches"))
        }
        val loaded = helper.level.server.recipeManager.recipes.filter { it.id.identifier().namespace == "peripheralium" }
        check(loaded.size == 4) { "Expected four loaded recipes, got ${loaded.map { it.id }}" }
        check(loaded.all { it.value.display().isNotEmpty() }) { "Loaded recipes must expose native displays" }
    }

    @GameTest(template = "modulebehavior.empty")
    fun bothLanguagesContainEveryTrackedTranslation(helper: GameTestHelper) = helper.immediate {
        val output = PackOutput(Path.of("behavior-fixture"))
        val providers = listOf(PeripheraliumENLanguageProvider(output), PeripheraliumUALanguageProvider(output))
        for ((index, provider) in providers.withIndex()) {
            val language = listOf("en_us", "uk_ua")[index]
            val writes = mutableMapOf<Path, JsonObject>()
            provider.run(
                CachedOutput { path, bytes, _ ->
                    check(writes.put(path, JsonParser.parseString(bytes.toString(Charsets.UTF_8)).asJsonObject) == null)
                },
            ).join()
            val path = Path.of("behavior-fixture/assets/peripheralium/lang/$language.json")
            check(writes.keys == setOf(path))
            val json = writes.getValue(path)
            check(json.keySet() == provider.getExpectedKeys().toList().toSet())
            provider.getExpectedKeys().forEach { check(json[it].asString.isNotBlank()) { "Missing translation $it" } }
            check(json["text.peripheralium.creative_tab"].asString == listOf("Peripheralium", "Перифераліум")[index])
            // Language files are client assets, not server data-pack resources.
            val stream = site.siredvin.peripheralium.PeripheraliumCore::class.java.getResourceAsStream("/assets/peripheralium/lang/$language.json") ?: error("Missing packaged language $language")
            val packaged = stream.bufferedReader().use { JsonParser.parseReader(it).asJsonObject }
            check(packaged == json) { "Packaged language differs from its provider: $language" }
        }
    }

    @GameTest(template = "modulebehavior.empty")
    fun blockLootDropsExactlyOneRegisteredBlock(helper: GameTestHelper) = helper.immediate {
        val block = Blocks.PERIPHERALIUM_BLOCK.get()
        val key = block.lootTable.orElseThrow()
        check(key.identifier().toString() == "peripheralium:blocks/peripheralium_block")
        val position = helper.absolutePos(BlockPos(1, 1, 1))
        helper.level.setBlockAndUpdate(position, block.defaultBlockState())
        check(helper.level.destroyBlock(position, true))
        helper.assertItemEntityCountIs(block.asItem(), BlockPos(1, 1, 1), 1.0, 1)
    }
}
