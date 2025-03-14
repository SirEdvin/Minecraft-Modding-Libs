package site.siredvin.broccolium.modules.platform.api

import net.minecraft.core.HolderGetter
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import java.util.*

interface RegistryLookup<T> :
    HolderGetter<T>,
    Iterable<T> {
    fun getKey(something: T): ResourceLocation
    fun getResourceKey(something: T): Optional<ResourceKey<T>>
    fun get(location: ResourceLocation): T

    fun tryGet(location: ResourceLocation): T?

    fun keySet(): List<ResourceLocation>
}
