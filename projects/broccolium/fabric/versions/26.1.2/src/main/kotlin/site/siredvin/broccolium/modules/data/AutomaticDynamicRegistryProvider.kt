package site.siredvin.broccolium.modules.data

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider
import net.fabricmc.fabric.api.event.registry.DynamicRegistries
import net.minecraft.core.HolderLookup
import net.minecraft.core.RegistrySetBuilder
import java.util.concurrent.CompletableFuture

class AutomaticDynamicRegistryProvider(private val name: String, output: FabricPackOutput, registries: CompletableFuture<RegistrySetBuilder.PatchedRegistries>) : FabricDynamicRegistryProvider(output, registries.thenApply(RegistrySetBuilder.PatchedRegistries::patches)) {
    override fun configure(registries: HolderLookup.Provider, entries: Entries) {
        for (registry in DynamicRegistries.getWorldRegistries()) entries.addAll(registries.lookupOrThrow(registry.key()))
    }

    override fun getName(): String = name
}
