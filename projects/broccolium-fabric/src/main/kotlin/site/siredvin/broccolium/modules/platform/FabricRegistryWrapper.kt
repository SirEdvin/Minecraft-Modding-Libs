package site.siredvin.broccolium.modules.platform

import net.minecraft.core.Holder
import net.minecraft.core.HolderSet
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import site.siredvin.broccolium.modules.platform.api.RegistryWrapper
import java.util.*

class FabricRegistryWrapper<T>(private val name: ResourceLocation, private val registry: Registry<T>) : RegistryWrapper<T> {
    override fun getId(something: T): Int {
        val id = registry.getId(something)
        if (id == -1) throw IllegalArgumentException()
        return id
    }

    override fun getKey(something: T): ResourceLocation = registry.getKey(something!!) ?: throw IllegalArgumentException()

    override fun get(location: ResourceLocation): T = registry.get(location) ?: throw IllegalArgumentException()

    override fun get(id: Int): T = registry.byId(id) ?: throw IllegalArgumentException()

    override fun get(tagKey: TagKey<T>): Optional<HolderSet.Named<T>> = registry.getTag(tagKey)

    override fun get(resourceKey: ResourceKey<T>): Optional<Holder.Reference<T>> = registry.getHolder(resourceKey)

    override fun tryGet(location: ResourceLocation): T? = registry.get(location)

    override fun iterator(): Iterator<T> = registry.iterator()

    override fun keySet(): Set<ResourceLocation> = registry.keySet()
}
