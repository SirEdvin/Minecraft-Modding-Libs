package site.siredvin.broccolium.modules.data.recipe

import com.google.common.collect.Lists
import com.google.common.collect.Maps
import net.minecraft.advancements.Criterion
import net.minecraft.data.recipes.RecipeBuilder
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.ShapedRecipe
import net.minecraft.world.item.crafting.ShapedRecipePattern
import net.minecraft.world.level.ItemLike
import java.util.*

class TweakedShapedRecipeBuilder(private val result: ItemStack, private val category: RecipeCategory = RecipeCategory.MISC) : RecipeBuilder {
    private val rows: MutableList<String> = Lists.newArrayList()
    private val key: MutableMap<Char, Ingredient> = Maps.newLinkedHashMap()
    private var group: String? = null

    fun define(char: Char, ing: TagKey<Item>): TweakedShapedRecipeBuilder = this.define(char, Ingredient.of(ing))

    fun define(char: Char, ing: ItemLike): TweakedShapedRecipeBuilder = this.define(char, Ingredient.of(*arrayOf(ing)))

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

    override fun getResult(): Item = result.item

    override fun save(output: RecipeOutput, id: ResourceLocation) {
        val pattern = ShapedRecipePattern.of(this.key, this.rows)
        val recipe = ShapedRecipe(
            Objects.requireNonNullElse(this.group, "") as String,
            RecipeBuilder.determineBookCategory(
                this.category,
            ),
            pattern,
            this.result,
            false,
        )
        output.accept(id, recipe, null)
    }
}
