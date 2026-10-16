package site.siredvin.peripheralium.testmod

import com.google.gson.JsonObject
import com.mojang.serialization.JsonOps
import net.minecraft.advancements.Advancement
import net.minecraft.advancements.AdvancementHolder
import net.minecraft.data.PackOutput
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.gametest.framework.GameTestHelper
import net.minecraft.resources.RegistryOps
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.crafting.Recipe
import site.siredvin.broccolium.modules.data.recipe.TweakedShapedRecipeBuilder
import site.siredvin.peripheralium.Items
import site.siredvin.peripheralium.data.PeripheraliumRecipeProvider
import java.nio.file.Path
import java.util.concurrent.CompletableFuture

internal object RecipeAssertions {
    val expectedItems = setOf("peripheralium_dust", "peripheralium_block", "peripheralium_upgrade_template")

    fun verify(helper: GameTestHelper) {
        val emitted = mutableMapOf<String, JsonObject>()
        val ops = RegistryOps.create(JsonOps.INSTANCE, helper.level.registryAccess())
        val output = object : RecipeOutput {
            override fun accept(id: ResourceLocation, recipe: Recipe<*>, advancement: AdvancementHolder?) {
                check(advancement == null)
                check(emitted.put(id.path, Recipe.CODEC.encodeStart(ops, recipe).orThrow.asJsonObject) == null) { "Duplicate recipe $id" }
                check(id.namespace == "peripheralium")
            }
            override fun advancement(): Advancement.Builder = error("These recipes must not emit advancement builders")
        }
        val provider = PeripheraliumRecipeProvider(PackOutput(Path.of("unused-recipe-output")), CompletableFuture.completedFuture(helper.level.registryAccess()))
        PeripheraliumRecipeProvider::class.java.getDeclaredMethod("buildRecipes", RecipeOutput::class.java).apply { isAccessible = true }.invoke(provider, output)
        verifyRecipeJson(emitted, "id", legacy = false)
        expectIllegalArgument { TweakedShapedRecipeBuilder(Items.PERIPHERALIUM_DUST.get()).define('P', Items.PERIPHERALIUM_DUST.get()).define('P', Items.PERIPHERALIUM_DUST.get()) }
        expectIllegalArgument { TweakedShapedRecipeBuilder(Items.PERIPHERALIUM_DUST.get()).pattern("PP").pattern("P") }
    }
}
