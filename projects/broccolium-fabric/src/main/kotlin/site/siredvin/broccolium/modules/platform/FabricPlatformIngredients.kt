package site.siredvin.broccolium.modules.platform

import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.Ingredient
import site.siredvin.broccolium.modules.platform.api.InnerPlatformIngredients

object FabricPlatformIngredients : InnerPlatformIngredients {
    override val redstone: Ingredient
        get() = Ingredient.of(ConventionalItemTags.REDSTONE_DUSTS)
    override val glowstoneDust: Ingredient
        get() = Ingredient.of(Items.GLOWSTONE_DUST)

    override val xpBottle: Ingredient
        get() = Ingredient.of(Items.EXPERIENCE_BOTTLE)
}
