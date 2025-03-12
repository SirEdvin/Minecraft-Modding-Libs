package site.siredvin.broccolium.modules.data

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider
import net.fabricmc.fabric.api.event.registry.DynamicRegistries
import net.minecraft.core.HolderLookup
import net.minecraft.core.RegistrySetBuilder
import java.util.concurrent.CompletableFuture

class AutomaticDynamicRegistryProvider(output: FabricDataOutput, registries: CompletableFuture<RegistrySetBuilder.PatchedRegistries>) : FabricDynamicRegistryProvider(output, registries.thenApply(RegistrySetBuilder.PatchedRegistries::patches)) {
    override fun configure(registries: HolderLookup.Provider, entries: Entries) {
        for (r in DynamicRegistries.getDynamicRegistries()) entries.addAll(registries.lookupOrThrow(r.key()))
    }

    override fun getName(): String = "Registries"
}
