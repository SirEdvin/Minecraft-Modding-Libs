package site.siredvin.broccolium.modules.data

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider
import net.fabricmc.fabric.api.event.registry.DynamicRegistries
import net.minecraft.core.HolderLookup
import net.minecraft.core.RegistrySetBuilder
import java.util.concurrent.CompletableFuture

class AutomaticDynamicRegistryProvider(private val name: String, output: FabricPackOutput, registries: CompletableFuture<RegistrySetBuilder.PatchedRegistries>) : FabricDynamicRegistryProvider(output, registries.thenApply(RegistrySetBuilder.PatchedRegistries::patches)) {
    override fun configure(registries: HolderLookup.Provider, entries: Entries) {
        // A registry patch need not contain every world registry (notably
        // dimensions). Serialize the registries actually present in the patch.
        for (registry in DynamicRegistries.getWorldRegistries()) registries.lookup(registry.key()).ifPresent { lookup ->
            lookup.listElements().filter { it.key().identifier().namespace == name }.forEach { entries.add(it) }
        }
    }

    override fun getName(): String = name
}
