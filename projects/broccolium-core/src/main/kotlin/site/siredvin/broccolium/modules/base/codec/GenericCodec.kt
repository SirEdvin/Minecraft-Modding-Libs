package site.siredvin.broccolium.modules.base.codec

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import site.siredvin.broccolium.modules.platform.api.RegistryEntry

object GenericCodec {
    fun <T> codec(registry: Registry<T>): Codec<RegistryEntry<out T>> = ResourceLocation.CODEC.flatXmap({ id ->
        registry.getHolder(ResourceKey.create(registry.key(), id)).map { x -> DataResult.success(HolderEntry(id, x)) }.orElseGet { DataResult.error { "Unknown registry key in " + registry.key() + ": " + id } }
    }, { holder -> DataResult.success(holder.id) })
}
