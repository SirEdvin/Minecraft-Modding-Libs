package site.siredvin.broccolium.modules.platform

import net.minecraft.core.Holder
import net.minecraft.core.HolderLookup
import net.minecraft.core.HolderSet
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import site.siredvin.broccolium.modules.platform.api.RegistryLookup
import java.util.*
import kotlin.jvm.optionals.getOrNull

class FabricLookupWrapper<T>(private val key: ResourceKey<Registry<T>>, private val registry: HolderLookup.RegistryLookup<T>) : RegistryLookup<T> {
    override fun getKey(something: T): ResourceLocation = getResourceKey(something).get().location()

    override fun getResourceKey(something: T): Optional<ResourceKey<T>> = registry.listElements().filter { it.value() == something }.findFirst().map { it.key() }

    override fun get(location: ResourceLocation): T = tryGet(location) ?: throw IllegalArgumentException()

    override fun get(tagKey: TagKey<T>): Optional<HolderSet.Named<T>> = registry.get(tagKey)

    override fun get(resourceKey: ResourceKey<T>): Optional<Holder.Reference<T>> = registry.get(resourceKey)

    override fun tryGet(location: ResourceLocation): T? = registry.get(ResourceKey.create(key, location)).getOrNull()?.value()

    override fun iterator(): Iterator<T> = registry.listElements().map { it.value() }.iterator()

    override fun keySet(): List<ResourceLocation> = registry.listElements().map { it.key().location() }.toList()
}
