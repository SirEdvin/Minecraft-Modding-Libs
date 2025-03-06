package site.siredvin.broccolium.modules.data.recipe

import net.minecraft.advancements.Criterion
import net.minecraft.core.NonNullList
import net.minecraft.data.recipes.RecipeBuilder
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.ShapelessRecipe
import net.minecraft.world.level.ItemLike
import java.util.*

class TweakedShapelessRecipeBuilder(private val result: ItemLike, private val count: Int = 1, private val category: RecipeCategory = RecipeCategory.MISC) : RecipeBuilder {
    private val ingredients: NonNullList<Ingredient> = NonNullList.create()
    private var group: String? = null

    fun requires(ing: TagKey<Item>, count: Int = 1): TweakedShapelessRecipeBuilder = this.requires(Ingredient.of(ing), count)

    fun requires(ing: ItemLike, count: Int = 1): TweakedShapelessRecipeBuilder {
        for (i in 0..<count) {
            this.requires(Ingredient.of(*arrayOf(ing)))
        }

        return this
    }

    fun requires(ing: Ingredient, count: Int = 1): TweakedShapelessRecipeBuilder {
        for (i in 0..<count) {
            ingredients.add(ing)
        }
        return this
    }

    override fun unlockedBy(id: String, criterion: Criterion<*>): TweakedShapelessRecipeBuilder = this

    override fun group(group: String?): TweakedShapelessRecipeBuilder {
        this.group = group
        return this
    }

    override fun getResult(): Item = this.result.asItem()

    override fun save(output: RecipeOutput, id: ResourceLocation) {
        val recipe = ShapelessRecipe(
            Objects.requireNonNullElse(this.group, "") as String,
            RecipeBuilder.determineBookCategory(
                this.category,
            ),
            ItemStack(this.result, this.count),
            this.ingredients,
        )
        output.accept(id, recipe, null)
    }
}
