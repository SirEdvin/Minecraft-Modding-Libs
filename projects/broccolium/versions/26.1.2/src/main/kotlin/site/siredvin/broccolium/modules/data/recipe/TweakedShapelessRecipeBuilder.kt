package site.siredvin.broccolium.modules.data.recipe

import net.minecraft.advancements.Criterion
import net.minecraft.core.HolderGetter
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.data.recipes.RecipeBuilder
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.resources.ResourceKey
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.ItemStackTemplate
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.ShapelessRecipe
import net.minecraft.world.level.ItemLike

class TweakedShapelessRecipeBuilder(
    private val result: ItemStackTemplate,
    private val category: RecipeCategory = RecipeCategory.MISC,
    private val items: HolderGetter<Item> = BuiltInRegistries.ITEM,
) : RecipeBuilder {
    private val ingredients = mutableListOf<Ingredient>()
    private var group: String? = null

    constructor(result: Item, category: RecipeCategory = RecipeCategory.MISC) : this(ItemStackTemplate(result), category)
    constructor(result: ItemStack, category: RecipeCategory = RecipeCategory.MISC) : this(ItemStackTemplate.fromNonEmptyStack(result), category)

    fun requires(ing: TagKey<Item>, count: Int = 1): TweakedShapelessRecipeBuilder = requires(Ingredient.of(items.getOrThrow(ing)), count)

    fun requires(ing: ItemLike, count: Int = 1): TweakedShapelessRecipeBuilder = requires(Ingredient.of(ing), count)

    fun requires(ing: Ingredient, count: Int = 1): TweakedShapelessRecipeBuilder {
        for (i in 0..<count) ingredients.add(ing)
        return this
    }

    override fun unlockedBy(id: String, criterion: Criterion<*>): TweakedShapelessRecipeBuilder = this

    override fun group(group: String?): TweakedShapelessRecipeBuilder {
        this.group = group
        return this
    }

    fun getResult(): Item = result.item().value()

    override fun defaultId(): ResourceKey<Recipe<*>> = RecipeBuilder.getDefaultRecipeId(result)

    override fun save(output: RecipeOutput, id: ResourceKey<Recipe<*>>) {
        val recipe = ShapelessRecipe(
            RecipeBuilder.createCraftingCommonInfo(true),
            RecipeBuilder.createCraftingBookInfo(category, group ?: ""),
            result,
            ingredients.toList(),
        )
        output.accept(id, recipe, null)
    }
}
