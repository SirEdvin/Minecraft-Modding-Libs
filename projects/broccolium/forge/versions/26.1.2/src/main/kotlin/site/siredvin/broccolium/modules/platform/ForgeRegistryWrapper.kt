package site.siredvin.broccolium.modules.platform

import net.minecraft.core.Holder
import net.minecraft.core.HolderSet
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.Identifier
import net.minecraft.tags.TagKey
import site.siredvin.broccolium.modules.platform.api.RegistryWrapper
import java.util.Optional

class ForgeRegistryWrapper<T : Any>(private val name: Identifier, private val registry: Registry<T>) : RegistryWrapper<T> {
    override fun getId(something: T): Int {
        val id = registry.getId(something)
        require(id != -1) { "Unregistered value in $name" }
        return id
    }

    override fun getKey(something: T): Identifier = registry.getKey(something) ?: throw IllegalArgumentException("Unregistered value in $name")
    override fun getResourceKey(something: T): Optional<ResourceKey<T>> = registry.getResourceKey(something)
    override fun byNameCodec(): com.mojang.serialization.Codec<T> = registry.byNameCodec()
    override fun get(location: Identifier): T = registry.get(location).orElseThrow { IllegalArgumentException("Unknown $name value $location") }.value()
    override fun get(id: Int): T = registry.get(id).orElseThrow { IllegalArgumentException("Unknown $name value $id") }.value()
    override fun get(tagKey: TagKey<T>): Optional<HolderSet.Named<T>> = registry.get(tagKey)
    override fun get(resourceKey: ResourceKey<T>): Optional<Holder.Reference<T>> = registry.get(resourceKey)
    override fun tryGet(location: Identifier): T? = registry.get(location).orElse(null)?.value()
    override fun iterator(): Iterator<T> = registry.iterator()
    override fun keySet(): Set<Identifier> = registry.keySet()
}
