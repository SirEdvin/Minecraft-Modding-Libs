package site.siredvin.peripheralium.data

import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.data.recipes.RecipeProvider
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.crafting.Ingredient
import site.siredvin.broccolium.modules.data.recipe.TweakedShapedRecipeBuilder
import site.siredvin.broccolium.modules.data.recipe.TweakedShapelessRecipeBuilder
import site.siredvin.broccolium.modules.platform.PlatformIngredients
import site.siredvin.peripheralium.Blocks
import site.siredvin.peripheralium.Items
import java.util.concurrent.CompletableFuture

class PeripheraliumRecipeProvider(output: PackOutput, registries: CompletableFuture<HolderLookup.Provider>) : RecipeProvider(output, registries) {

    override fun buildRecipes(consumer: RecipeOutput) {
        val ingredients = PlatformIngredients.get()

        TweakedShapelessRecipeBuilder(Items.PERIPHERALIUM_DUST.get())
            .requires(ingredients.redstone)
            .requires(ingredients.glowstoneDust)
            .save(consumer)

        TweakedShapelessRecipeBuilder(Blocks.PERIPHERALIUM_BLOCK.get())
            .requires(Items.PERIPHERALIUM_DUST.get(), 9)
            .save(consumer)

        TweakedShapelessRecipeBuilder(Items.PERIPHERALIUM_DUST.get(), 9)
            .requires(Blocks.PERIPHERALIUM_BLOCK.get().asItem())
            .save(consumer, ResourceLocation.parse("peripheralium:peripheralium_block_uncraft"))

        TweakedShapedRecipeBuilder(Items.PERIPHERALIUM_UPGRADE_TEMPLATE.get(), 4)
            .define('P', Ingredient.of(Items.PERIPHERALIUM_DUST.get()))
            .define('X', ingredients.xpBottle)
            .pattern("PPP")
            .pattern("PXP")
            .pattern("P P")
            .save(consumer)
    }
}
