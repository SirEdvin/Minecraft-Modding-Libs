package site.siredvin.broccolium.modules.platform

import net.minecraft.resources.Identifier
import site.siredvin.broccolium.modules.platform.api.RegistryEntry
import java.util.function.Supplier

class SimpleRegistryEntry<T>(override val id: Identifier, private val sup: Supplier<T>) : RegistryEntry<T> {
    override fun get(): T = sup.get()
}
