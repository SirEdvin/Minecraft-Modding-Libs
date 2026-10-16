package site.siredvin.peripheralium.testmod

import com.google.gson.JsonObject
import net.minecraft.data.PackOutput
import net.minecraft.data.recipes.FinishedRecipe
import net.minecraft.gametest.framework.GameTestHelper
import net.minecraft.resources.ResourceLocation
import site.siredvin.broccolium.modules.data.recipe.TweakedShapedRecipeBuilder
import site.siredvin.peripheralium.Items
import site.siredvin.peripheralium.data.PeripheraliumRecipeProvider
import java.nio.file.Path
import java.util.function.Consumer

internal object RecipeAssertions {
    val expectedItems = setOf("peripheralium_dust", "peripheralium_block", "peripheralium_upgrade_template", "peripheralium_blend")

    fun verify(helper: GameTestHelper) {
        val emitted = mutableMapOf<String, JsonObject>()
        val output = Consumer<FinishedRecipe> {
            check(it.serializeAdvancement() == null && it.advancementId == null)
            check(emitted.put(it.id.path, it.serializeRecipe()) == null) { "Duplicate recipe ${it.id}" }
            check(it.id.namespace == "peripheralium")
        }
        val provider = PeripheraliumRecipeProvider(PackOutput(Path.of("unused-recipe-output")))
        PeripheraliumRecipeProvider::class.java.getDeclaredMethod("buildRecipes", Consumer::class.java).apply { isAccessible = true }.invoke(provider, output)
        verifyRecipeJson(emitted, "item", legacy = true)
        val smelting = emitted.getValue("peripheralium_dust_smelting")
        check(smelting["type"].asString == "minecraft:smelting")
        check(smelting["experience"].asFloat == 0.7f && smelting["cookingtime"].asInt == 200)
        check(smelting["ingredient"].asJsonObject["item"].asString == "peripheralium:peripheralium_blend")
        check(smelting["result"].asString == "peripheralium:peripheralium_dust")
        expectIllegalArgument { TweakedShapedRecipeBuilder.shaped(Items.PERIPHERALIUM_DUST.get()).define('P', Items.PERIPHERALIUM_DUST.get()).define('P', Items.PERIPHERALIUM_DUST.get()) }
        expectIllegalArgument { TweakedShapedRecipeBuilder.shaped(Items.PERIPHERALIUM_DUST.get()).pattern("PP").pattern("P") }
        var emittedInvalid = false
        try {
            TweakedShapedRecipeBuilder.shaped(Items.PERIPHERALIUM_DUST.get()).pattern("QQ").save({ emittedInvalid = true }, ResourceLocation("peripheralium", "invalid"))
            error("Undefined ingredient was accepted")
        } catch (expected: IllegalStateException) {
            check(expected.message!!.contains("undefined symbol"))
        }
        check(!emittedInvalid)
    }
}
