package site.siredvin.broccolium.modules.platform

import net.minecraft.resources.ResourceLocation
import site.siredvin.broccolium.modules.platform.api.RegistryEntry
import java.util.function.Supplier

class SimpleRegistryEntry<T>(override val id: ResourceLocation, private val sup: Supplier<T>) : RegistryEntry<T> {
    override fun get(): T = sup.get()
}
