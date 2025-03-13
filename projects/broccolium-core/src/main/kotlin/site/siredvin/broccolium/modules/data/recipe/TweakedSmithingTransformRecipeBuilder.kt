package site.siredvin.broccolium.modules.data.recipe

import net.minecraft.data.recipes.RecipeBuilder
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.SmithingTransformRecipe

class TweakedSmithingTransformRecipeBuilder(
    private val template: Ingredient,
    private val base: Ingredient,
    private val addition: Ingredient,
    private val result: ItemStack,
) {

    constructor(template: Ingredient, base: Ingredient, addition: Ingredient, result: Item) : this(template, base, addition, ItemStack(result))

    fun save(output: RecipeOutput, id: String) {
        this.save(output, ResourceLocation.parse(id))
    }

    fun save(consumer: RecipeOutput?) {
        this.save(consumer!!, RecipeBuilder.getDefaultRecipeId(result.item))
    }

    fun save(output: RecipeOutput, id: ResourceLocation) {
        val recipe = SmithingTransformRecipe(
            this.template,
            this.base,
            this.addition,
            this.result,
        )
        output.accept(id, recipe, null)
    }
}
