package site.siredvin.broccolium.modules.data.recipe

import net.minecraft.advancements.Criterion
import net.minecraft.core.component.DataComponents
import net.minecraft.data.recipes.*
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.*
import net.minecraft.world.level.ItemLike
import java.util.*

class TweakedCookingRecipeBuilder private constructor(
    private val bookCategory: CookingBookCategory,
    private val result: ItemLike,
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

    override fun getResult(): Item = this.result.asItem()

    override fun save(output: RecipeOutput, id: ResourceLocation) {
        val recipe = factory.create(
            Objects.requireNonNullElse(this.group, "") as String,
            this.bookCategory,
            this.ingredient,
            ItemStack(this.result),
            this.experience,
            this.cookingTime,
        )
        output.accept(id, recipe, null)
    }

    companion object {
        fun <T : AbstractCookingRecipe> generic(
            input: Ingredient,
            result: ItemLike,
            cookingTime: Float,
            count: Int,
            serializer: RecipeSerializer<T>,
            factory: AbstractCookingRecipe.Factory<T>,
        ): TweakedCookingRecipeBuilder = TweakedCookingRecipeBuilder(
            determineRecipeCategory(serializer, result),
            result,
            input,
            cookingTime,
            count,
            factory,
        )

        fun campfireCooking(
            input: Ingredient,
            output: ItemLike,
            cookingTime: Float,
            count: Int,
        ): TweakedCookingRecipeBuilder = TweakedCookingRecipeBuilder(
            CookingBookCategory.FOOD,
            output,
            input,
            cookingTime,
            count,
        ) { `$$0`: String, `$$1`: CookingBookCategory, `$$2`: Ingredient, `$$3`: ItemStack, `$$4`: Float, `$$5`: Int ->
            CampfireCookingRecipe(
                `$$0`,
                `$$1`,
                `$$2`,
                `$$3`,
                `$$4`,
                `$$5`,
            )
        }

        fun blasting(
            input: Ingredient,
            output: ItemLike,
            cookingTime: Float,
            count: Int,
        ): TweakedCookingRecipeBuilder = TweakedCookingRecipeBuilder(
            determineBlastingRecipeCategory(output),
            output,
            input,
            cookingTime,
            count,
        ) { `$$0`: String, `$$1`: CookingBookCategory, `$$2`: Ingredient, `$$3`: ItemStack, `$$4`: Float, `$$5`: Int ->
            BlastingRecipe(
                `$$0`,
                `$$1`,
                `$$2`,
                `$$3`,
                `$$4`,
                `$$5`,
            )
        }

        fun smelting(
            input: Ingredient,
            output: ItemLike,
            cookingTime: Float,
            count: Int,
        ): TweakedCookingRecipeBuilder = TweakedCookingRecipeBuilder(
            determineSmeltingRecipeCategory(output),
            output,
            input,
            cookingTime,
            count,
        ) { `$$0`: String, `$$1`: CookingBookCategory, `$$2`: Ingredient, `$$3`: ItemStack, `$$4`: Float, `$$5`: Int ->
            SmeltingRecipe(
                `$$0`,
                `$$1`,
                `$$2`,
                `$$3`,
                `$$4`,
                `$$5`,
            )
        }

        fun smoking(
            input: Ingredient,
            output: ItemLike,
            cookingTime: Float,
            count: Int,
        ): TweakedCookingRecipeBuilder = TweakedCookingRecipeBuilder(
            CookingBookCategory.FOOD,
            output,
            input,
            cookingTime,
            count,
        ) { `$$0`: String, `$$1`: CookingBookCategory, `$$2`: Ingredient, `$$3`: ItemStack, `$$4`: Float, `$$5`: Int ->
            SmokingRecipe(
                `$$0`,
                `$$1`,
                `$$2`,
                `$$3`,
                `$$4`,
                `$$5`,
            )
        }

        private fun determineSmeltingRecipeCategory(result: ItemLike): CookingBookCategory = if (result.asItem().components().has(DataComponents.FOOD)) {
            CookingBookCategory.FOOD
        } else {
            if (result.asItem() is BlockItem) CookingBookCategory.BLOCKS else CookingBookCategory.MISC
        }

        private fun determineBlastingRecipeCategory(result: ItemLike): CookingBookCategory = if (result.asItem() is BlockItem) CookingBookCategory.BLOCKS else CookingBookCategory.MISC

        private fun determineRecipeCategory(
            serializer: RecipeSerializer<out AbstractCookingRecipe>,
            result: ItemLike,
        ): CookingBookCategory {
            if (serializer === RecipeSerializer.SMELTING_RECIPE) {
                return determineSmeltingRecipeCategory(result)
            } else if (serializer === RecipeSerializer.BLASTING_RECIPE) {
                return determineBlastingRecipeCategory(result)
            } else {
                check(!(serializer !== RecipeSerializer.SMOKING_RECIPE && serializer !== RecipeSerializer.CAMPFIRE_COOKING_RECIPE)) { "Unknown cooking recipe type" }
            }
            return CookingBookCategory.FOOD
        }
    }
}
