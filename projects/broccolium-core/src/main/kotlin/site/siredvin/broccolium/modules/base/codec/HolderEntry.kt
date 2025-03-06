package site.siredvin.broccolium.modules.base.codec

import net.minecraft.core.Holder
import net.minecraft.resources.ResourceLocation
import site.siredvin.broccolium.modules.platform.api.RegistryEntry

class HolderEntry<T>(override val id: ResourceLocation, private val holder: Holder<T>) : RegistryEntry<T> {
    override fun get(): T = holder.value()
}
