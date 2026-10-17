package site.siredvin.broccolium.modules.platform.api

import com.mojang.serialization.Codec
import net.minecraft.core.HolderGetter
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import java.util.*

interface RegistryWrapper<T : Any> :
    HolderGetter<T>,
    Iterable<T> {
    fun getId(something: T): Int
    fun getKey(something: T): Identifier
    fun getResourceKey(something: T): Optional<ResourceKey<T>>
    fun get(location: Identifier): T

    fun tryGet(location: Identifier): T?
    fun get(id: Int): T

    fun keySet(): Set<Identifier>
    fun byNameCodec(): Codec<T>
}
