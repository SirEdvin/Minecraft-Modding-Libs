package site.siredvin.peripheralium.testmod

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.minecraft.data.CachedOutput
import net.minecraft.data.PackOutput
import net.minecraft.gametest.framework.GameTestHelper
import site.siredvin.broccolium.modules.data.recipe.TweakedShapedRecipeBuilder
import site.siredvin.peripheralium.Items
import site.siredvin.peripheralium.data.PeripheraliumRecipeProvider
import java.nio.file.Path
import java.util.concurrent.CompletableFuture

internal object RecipeAssertions {
    val expectedItems = setOf("peripheralium_dust", "peripheralium_block", "peripheralium_upgrade_template")

    fun verify(helper: GameTestHelper) {
        val emitted = mutableMapOf<String, JsonObject>()
        val output = PackOutput(Path.of("unused-recipe-output"))
        val provider = PeripheraliumRecipeProvider(output, CompletableFuture.completedFuture(helper.level.registryAccess()))
        provider.run(
            CachedOutput { path, bytes, _ ->
                check(path.startsWith(Path.of("unused-recipe-output/data/peripheralium/recipe"))) { "Unexpected output $path" }
                val id = path.fileName.toString().removeSuffix(".json")
                check(emitted.put(id, JsonParser.parseString(bytes.toString(Charsets.UTF_8)).asJsonObject) == null)
            },
        ).join()
        verifyRecipeJson(emitted, "id", legacy = false)
        expectIllegalArgument { TweakedShapedRecipeBuilder(Items.PERIPHERALIUM_DUST.get()).define('P', Items.PERIPHERALIUM_DUST.get()).define('P', Items.PERIPHERALIUM_DUST.get()) }
        expectIllegalArgument { TweakedShapedRecipeBuilder(Items.PERIPHERALIUM_DUST.get()).pattern("PP").pattern("P") }
    }
}
