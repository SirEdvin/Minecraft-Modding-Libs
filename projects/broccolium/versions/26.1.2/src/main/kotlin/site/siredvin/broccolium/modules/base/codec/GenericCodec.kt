package site.siredvin.broccolium.modules.base.codec

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import net.minecraft.core.Registry
import net.minecraft.resources.Identifier
import site.siredvin.broccolium.modules.platform.api.RegistryEntry

object GenericCodec {
    fun <T : Any> codec(registry: Registry<T>): Codec<RegistryEntry<out T>> = Identifier.CODEC.flatXmap({ id ->
        registry.get(id).map { holder -> DataResult.success<RegistryEntry<out T>>(HolderEntry(id, holder)) }
            .orElseGet { DataResult.error { "Unknown registry key in ${registry.key()}: $id" } }
    }, { holder -> DataResult.success(holder.id) })
}
