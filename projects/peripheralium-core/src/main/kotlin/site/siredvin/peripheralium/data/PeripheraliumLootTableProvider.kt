package site.siredvin.peripheralium.data

import net.minecraft.data.loot.LootTableProvider
import net.minecraft.data.loot.LootTableSubProvider
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import site.siredvin.broccolium.modules.data.loot.LootTableHelper
import site.siredvin.peripheralium.Blocks
import site.siredvin.peripheralium.PeripheraliumPlatform
import java.util.function.BiConsumer

object PeripheraliumLootTableProvider {
    fun getTables(): List<LootTableProvider.SubProviderEntry> = listOf(
        LootTableProvider.SubProviderEntry({
            LootTableSubProvider {
                registerBlocks(it)
            }
        }, LootContextParamSets.BLOCK),
    )

    fun registerBlocks(consumer: BiConsumer<ResourceLocation, LootTable.Builder>) {
        val helper = LootTableHelper(PeripheraliumPlatform.holder)
        helper.dropSelf(consumer, Blocks.PERIPHERALIUM_BLOCK)
        helper.validate()
    }
}
