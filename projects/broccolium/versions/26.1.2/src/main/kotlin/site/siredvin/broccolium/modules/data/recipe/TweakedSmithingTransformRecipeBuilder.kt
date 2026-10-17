package site.siredvin.broccolium.modules.data.recipe

import net.minecraft.core.registries.Registries
import net.minecraft.data.recipes.RecipeBuilder
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.ItemStackTemplate
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.SmithingTransformRecipe
import java.util.Optional

class TweakedSmithingTransformRecipeBuilder(
    private val template: Ingredient,
    private val base: Ingredient,
    private val addition: Ingredient,
    private val result: ItemStackTemplate,
) {
    constructor(template: Ingredient, base: Ingredient, addition: Ingredient, result: Item) : this(template, base, addition, ItemStackTemplate(result))
    constructor(template: Ingredient, base: Ingredient, addition: Ingredient, result: ItemStack) : this(template, base, addition, ItemStackTemplate.fromNonEmptyStack(result))

    fun save(output: RecipeOutput, id: String) {
        save(output, Identifier.parse(id))
    }

    fun save(consumer: RecipeOutput) {
        save(consumer, RecipeBuilder.getDefaultRecipeId(result))
    }

    fun save(output: RecipeOutput, id: Identifier) {
        save(output, ResourceKey.create(Registries.RECIPE, id))
    }

    fun save(output: RecipeOutput, id: ResourceKey<Recipe<*>>) {
        val recipe = SmithingTransformRecipe(
            Recipe.CommonInfo(true),
            Optional.of(template),
            base,
            Optional.of(addition),
            result,
        )
        output.accept(id, recipe, null)
    }
}
