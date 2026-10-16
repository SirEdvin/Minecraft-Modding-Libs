package site.siredvin.peripheralium.testmod

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.data.CachedOutput
import net.minecraft.data.PackOutput
import net.minecraft.gametest.framework.GameTest
import net.minecraft.gametest.framework.GameTestAssertException
import net.minecraft.gametest.framework.GameTestHelper
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import net.minecraft.world.phys.Vec3
import site.siredvin.peripheralium.Blocks
import site.siredvin.peripheralium.Items
import site.siredvin.peripheralium.PeripheraliumPlatform
import site.siredvin.peripheralium.data.PeripheraliumENLanguageProvider
import site.siredvin.peripheralium.data.PeripheraliumLootTableProvider
import site.siredvin.peripheralium.data.PeripheraliumUALanguageProvider
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
        check(Items.PERIPHERALIUM_DUST.get() in PeripheraliumPlatform.holder.items.map { it.get() })
        check(Items.PERIPHERALIUM_UPGRADE_TEMPLATE.get() in PeripheraliumPlatform.holder.items.map { it.get() })
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
            check(expected.message!!.startsWith("No recipe matches"))
        }
    }

    @GameTest(template = "modulebehavior.empty")
    fun bothLanguagesContainEveryTrackedTranslation(helper: GameTestHelper) = helper.immediate {
        val output = PackOutput(Path.of("behavior-fixture"))
        val providers = listOf(PeripheraliumENLanguageProvider(output), PeripheraliumUALanguageProvider(output))
        val expectedPaths = listOf("en_us", "uk_ua")
        for ((index, provider) in providers.withIndex()) {
            val writes = mutableMapOf<Path, JsonObject>()
            provider.run(
                CachedOutput { path, bytes, _ ->
                    check(writes.put(path, JsonParser.parseString(bytes.toString(Charsets.UTF_8)).asJsonObject) == null)
                },
            ).join()
            val path = Path.of("behavior-fixture/assets/peripheralium/lang/${expectedPaths[index]}.json")
            check(writes.keys == setOf(path)) { "Wrong language output: ${writes.keys}" }
            val json = writes.getValue(path)
            provider.getExpectedKeys().forEach { check(json.has(it) && json[it].asString.isNotBlank()) { "Missing translation $it" } }
            check(json["text.peripheralium.creative_tab"].asString == listOf("Peripheralium", "Перифераліум")[index])
        }
    }

    @GameTest(template = "modulebehavior.empty")
    fun blockLootDropsExactlyOneRegisteredBlock(helper: GameTestHelper) = helper.immediate {
        val tables = mutableListOf<LootTable>()
        PeripheraliumLootTableProvider.registerBlocks { _, builder -> tables.add(builder.build()) }
        check(tables.size == 1)
        val params = LootParams.Builder(helper.level)
            .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(helper.absolutePos(net.minecraft.core.BlockPos.ZERO)))
            .withParameter(LootContextParams.BLOCK_STATE, Blocks.PERIPHERALIUM_BLOCK.get().defaultBlockState())
            .withParameter(LootContextParams.TOOL, ItemStack.EMPTY)
            .create(LootContextParamSets.BLOCK)
        val drops = tables.single().getRandomItems(params)
        check(drops.size == 1 && drops.single().item == Blocks.PERIPHERALIUM_BLOCK.get().asItem() && drops.single().count == 1)
    }
}
