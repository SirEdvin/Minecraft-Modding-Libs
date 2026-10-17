package site.siredvin.broccolium.modules.data.recipe

import net.minecraft.advancements.Criterion
import net.minecraft.core.component.DataComponents
import net.minecraft.data.recipes.RecipeBuilder
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.ItemStackTemplate
import net.minecraft.world.item.crafting.*

class TweakedCookingRecipeBuilder private constructor(
    private val bookCategory: CookingBookCategory,
    private val result: ItemStackTemplate,
    private val ingredient: Ingredient,
    private val experience: Float,
    private val cookingTime: Int,
    private val factory: AbstractCookingRecipe.Factory<*>,
) : RecipeBuilder {
    private var group: String? = null

    override fun unlockedBy(id: String, criterion: Criterion<*>): TweakedCookingRecipeBuilder = this

    override fun group(group: String?): TweakedCookingRecipeBuilder {
        this.group = group
        return this
    }

    fun getResult(): Item = result.item().value()

    override fun defaultId(): ResourceKey<Recipe<*>> = RecipeBuilder.getDefaultRecipeId(result)

    override fun save(output: RecipeOutput, id: ResourceKey<Recipe<*>>) {
        val recipe = factory.create(
            Recipe.CommonInfo(true),
            AbstractCookingRecipe.CookingBookInfo(bookCategory, group ?: ""),
            ingredient,
            result,
            experience,
            cookingTime,
        )
        output.accept(id, recipe, null)
    }

    companion object {
        fun <T : AbstractCookingRecipe> generic(
            input: Ingredient,
            result: ItemStack,
            cookingTime: Float,
            count: Int,
            serializer: RecipeSerializer<T>,
            factory: AbstractCookingRecipe.Factory<T>,
        ): TweakedCookingRecipeBuilder = TweakedCookingRecipeBuilder(
            determineRecipeCategory(serializer, result),
            ItemStackTemplate.fromNonEmptyStack(result),
            input,
            cookingTime,
            count,
            factory,
        )

        fun campfireCooking(input: Ingredient, output: ItemStack, cookingTime: Float, count: Int): TweakedCookingRecipeBuilder = TweakedCookingRecipeBuilder(
            CookingBookCategory.FOOD,
            ItemStackTemplate.fromNonEmptyStack(output),
            input,
            cookingTime,
            count,
            ::CampfireCookingRecipe,
        )

        fun blasting(input: Ingredient, output: ItemStack, cookingTime: Int, experience: Float): TweakedCookingRecipeBuilder = TweakedCookingRecipeBuilder(
            determineBlastingRecipeCategory(output),
            ItemStackTemplate.fromNonEmptyStack(output),
            input,
            experience,
            cookingTime,
            ::BlastingRecipe,
        )

        fun smelting(input: Ingredient, output: ItemStack, experience: Float, cookingTime: Int): TweakedCookingRecipeBuilder = TweakedCookingRecipeBuilder(
            determineSmeltingRecipeCategory(output),
            ItemStackTemplate.fromNonEmptyStack(output),
            input,
            experience,
            cookingTime,
            ::SmeltingRecipe,
        )

        fun smoking(input: Ingredient, output: ItemStack, experience: Float, cookingTime: Int): TweakedCookingRecipeBuilder = TweakedCookingRecipeBuilder(
            CookingBookCategory.FOOD,
            ItemStackTemplate.fromNonEmptyStack(output),
            input,
            experience,
            cookingTime,
            ::SmokingRecipe,
        )

        private fun determineSmeltingRecipeCategory(result: ItemStack): CookingBookCategory = if (result.has(DataComponents.FOOD)) {
            CookingBookCategory.FOOD
        } else {
            determineBlastingRecipeCategory(result)
        }

        private fun determineBlastingRecipeCategory(result: ItemStack): CookingBookCategory = if (result.item is BlockItem) CookingBookCategory.BLOCKS else CookingBookCategory.MISC

        private fun determineRecipeCategory(serializer: RecipeSerializer<out AbstractCookingRecipe>, result: ItemStack): CookingBookCategory = when (serializer) {
            SmeltingRecipe.SERIALIZER -> determineSmeltingRecipeCategory(result)
            BlastingRecipe.SERIALIZER -> determineBlastingRecipeCategory(result)
            SmokingRecipe.SERIALIZER, CampfireCookingRecipe.SERIALIZER -> CookingBookCategory.FOOD
            else -> error("Unknown cooking recipe type")
        }
    }
}
