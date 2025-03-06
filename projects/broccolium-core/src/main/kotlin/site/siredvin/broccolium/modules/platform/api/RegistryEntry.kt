package site.siredvin.broccolium.modules.platform.api

import net.minecraft.resources.ResourceLocation
import java.util.function.Supplier

interface RegistryEntry<U> : Supplier<U> {
    val id: ResourceLocation
}
