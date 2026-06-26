package site.siredvin.tweakium.modules.plugins

import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import site.siredvin.broccolium.modules.storage.base.api.SlottedAgnosticStorage

class InventoryPlugin(
    override val level: Level,
    override val storage: SlottedAgnosticStorage<ItemStack, Int>,
    override val inventoryTransferLimit: Int,
) : AbstractInventoryPlugin()
