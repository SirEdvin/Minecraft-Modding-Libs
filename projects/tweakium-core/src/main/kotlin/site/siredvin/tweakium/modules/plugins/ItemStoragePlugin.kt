package site.siredvin.tweakium.modules.plugins

import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import site.siredvin.broccolium.modules.storage.base.api.AgnosticStorage

class ItemStoragePlugin(
    override val storage: AgnosticStorage<ItemStack, Int>,
    override val level: Level,
    override val itemStorageTransferLimit: Int,
) : AbstractItemStoragePlugin()
