package site.siredvin.tweakium.modules.peripheral.util

import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.component.DataComponentType
import net.minecraft.world.item.ItemStackTemplate

internal object UpgradeTemplateUtil {
    // Called when an equipped item is resolved, never while defining upgrades.
    fun read(stack: ItemStackTemplate, crafting: ItemStackTemplate, components: List<DataComponentType<*>>): DataComponentPatch {
        val builder = DataComponentPatch.builder()
        for (component in components) {
            val data = stack.get(component)
            if (data != null) {
                @Suppress("UNCHECKED_CAST")
                builder.set(component as DataComponentType<Any>, data)
            } else if (crafting.get(component) != null || stack.components().entrySet().any { it.key == component && it.value.isEmpty }) {
                builder.remove(component)
            }
        }
        return builder.build()
    }

    // Overlay only the approved state components; do not bind item defaults or
    // create a mutable stack while constructing a definition-time template.
    fun write(crafting: ItemStackTemplate, data: DataComponentPatch, components: List<DataComponentType<*>>): ItemStackTemplate {
        if (data.isEmpty) return crafting
        val builder = DataComponentPatch.builder()
        crafting.components().entrySet().forEach { entry ->
            if (entry.value.isPresent) {
                @Suppress("UNCHECKED_CAST")
                builder.set(entry.key as DataComponentType<Any>, entry.value.get())
            } else {
                builder.remove(entry.key)
            }
        }
        for (entry in data.entrySet()) {
            if (entry.key !in components) continue
            if (entry.value.isPresent) {
                @Suppress("UNCHECKED_CAST")
                builder.set(entry.key as DataComponentType<Any>, entry.value.get())
            } else {
                builder.remove(entry.key)
            }
        }
        return ItemStackTemplate(crafting.item(), crafting.count(), builder.build())
    }
}
