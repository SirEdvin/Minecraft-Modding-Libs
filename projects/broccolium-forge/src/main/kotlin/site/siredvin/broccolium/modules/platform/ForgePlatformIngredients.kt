package site.siredvin.broccolium.modules.platform

import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.Ingredient
import net.minecraftforge.common.Tags
import site.siredvin.broccolium.modules.platform.api.InnerPlatformIngredients

object ForgePlatformIngredients : InnerPlatformIngredients {
    override val redstone: Ingredient
        get() = Ingredient.of(Tags.Items.DUSTS_REDSTONE)
    override val glowstoneDust: Ingredient
        get() = Ingredient.of(Tags.Items.DUSTS_GLOWSTONE)

    override val xpBottle: Ingredient
        get() = Ingredient.of(Items.EXPERIENCE_BOTTLE)
}
