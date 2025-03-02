package site.siredvin.tweakium.modules.plugins

import net.minecraft.world.level.Level
import site.siredvin.broccolium.modules.storage.item.api.AgnosticItemStorage

class ItemStoragePlugin(
    override val storage: AgnosticItemStorage,
    override val level: Level,
    override val itemStorageTransferLimit: Int,
) : AbstractItemStoragePlugin()
