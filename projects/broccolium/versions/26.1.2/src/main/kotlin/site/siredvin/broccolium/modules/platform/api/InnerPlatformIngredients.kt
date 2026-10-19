package site.siredvin.broccolium.modules.platform.api

import net.minecraft.core.HolderLookup
import net.minecraft.world.item.crafting.Ingredient

interface InnerPlatformIngredients {
    val redstone: Ingredient
    val glowstoneDust: Ingredient
    val xpBottle: Ingredient

    /** Datagen must resolve tag holders from its own lookup, before a server exists. */
    fun redstone(registries: HolderLookup.Provider): Ingredient = redstone
    fun glowstoneDust(registries: HolderLookup.Provider): Ingredient = glowstoneDust
}
