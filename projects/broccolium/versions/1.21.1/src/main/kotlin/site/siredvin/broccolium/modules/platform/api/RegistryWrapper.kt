package site.siredvin.broccolium.modules.platform.api

import com.mojang.serialization.Codec
import net.minecraft.core.HolderGetter
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import java.util.*

interface RegistryWrapper<T> :
    HolderGetter<T>,
    Iterable<T> {
    fun getId(something: T): Int
    fun getKey(something: T): ResourceLocation
    fun getResourceKey(something: T): Optional<ResourceKey<T>>
    fun get(location: ResourceLocation): T

    fun tryGet(location: ResourceLocation): T?
    fun get(id: Int): T

    fun keySet(): Set<ResourceLocation>
    fun byNameCodec(): Codec<T>
}
