package site.siredvin.tweakium.modules.plugins

import net.minecraft.world.level.Level
import site.siredvin.broccolium.modules.storage.item.api.SlottedAgnosticItemStorage

class InventoryPlugin(override val level: Level, override val storage: SlottedAgnosticItemStorage) : AbstractInventoryPlugin()
