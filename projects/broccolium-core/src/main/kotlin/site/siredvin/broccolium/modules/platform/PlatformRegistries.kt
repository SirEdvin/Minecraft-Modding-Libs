package site.siredvin.broccolium.modules.platform

import net.minecraft.core.registries.Registries

object PlatformRegistries {
    val ITEMS by lazy { PlatformToolkit.get().wrap(Registries.ITEM) }
    val BLOCKS by lazy { PlatformToolkit.get().wrap(Registries.BLOCK) }
    val FLUIDS by lazy { PlatformToolkit.get().wrap(Registries.FLUID) }
    val ENTITY_TYPES by lazy { PlatformToolkit.get().wrap(Registries.ENTITY_TYPE) }
    val RECIPE_TYPES by lazy { PlatformToolkit.get().wrap(Registries.RECIPE_TYPE) }
    val DATA_COMPONENT_TYPE by lazy { PlatformToolkit.get().wrap(Registries.DATA_COMPONENT_TYPE) }
}
