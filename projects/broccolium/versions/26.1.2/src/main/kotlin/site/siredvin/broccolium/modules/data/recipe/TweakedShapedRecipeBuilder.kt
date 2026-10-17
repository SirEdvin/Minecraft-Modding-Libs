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
import net.minecraft.world.item.crafting.ShapedRecipe
import net.minecraft.world.item.crafting.ShapedRecipePattern
import net.minecraft.world.level.ItemLike

class TweakedShapedRecipeBuilder(
    private val result: ItemStackTemplate,
    private val category: RecipeCategory = RecipeCategory.MISC,
    private val items: HolderGetter<Item> = BuiltInRegistries.ITEM,
) : RecipeBuilder {
    private val rows = mutableListOf<String>()
    private val key = linkedMapOf<Char, Ingredient>()
    private var group: String? = null

    constructor(result: Item, category: RecipeCategory = RecipeCategory.MISC) : this(ItemStackTemplate(result), category)
    constructor(result: ItemStack, category: RecipeCategory = RecipeCategory.MISC) : this(ItemStackTemplate.fromNonEmptyStack(result), category)

    fun define(char: Char, ing: TagKey<Item>): TweakedShapedRecipeBuilder = define(char, Ingredient.of(items.getOrThrow(ing)))

    fun define(char: Char, ing: ItemLike): TweakedShapedRecipeBuilder = define(char, Ingredient.of(ing))

    fun define(char: Char, ing: Ingredient): TweakedShapedRecipeBuilder {
        require(!key.containsKey(char)) { "Symbol '$char' is already defined!" }
        require(char != ' ') { "Symbol ' ' (whitespace) is reserved and cannot be defined" }
        key[char] = ing
        return this
    }

    fun pattern(line: String): TweakedShapedRecipeBuilder {
        require(!(rows.isNotEmpty() && line.length != rows[0].length)) { "Pattern must be the same width on every line!" }
        rows.add(line)
        return this
    }

    override fun unlockedBy(p0: String, p1: Criterion<*>): TweakedShapedRecipeBuilder = this

    override fun group(group: String?): TweakedShapedRecipeBuilder {
        this.group = group
        return this
    }

    fun getResult(): Item = result.item().value()

    override fun defaultId(): ResourceKey<Recipe<*>> = RecipeBuilder.getDefaultRecipeId(result)

    override fun save(output: RecipeOutput, id: ResourceKey<Recipe<*>>) {
        val recipe = ShapedRecipe(
            RecipeBuilder.createCraftingCommonInfo(false),
            RecipeBuilder.createCraftingBookInfo(category, group ?: ""),
            ShapedRecipePattern.of(key, rows),
            result,
        )
        output.accept(id, recipe, null)
    }
}
