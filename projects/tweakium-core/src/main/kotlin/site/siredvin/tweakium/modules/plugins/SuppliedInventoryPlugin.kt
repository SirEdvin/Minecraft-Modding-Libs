package site.siredvin.tweakium.modules.plugins

import net.minecraft.world.level.Level
import site.siredvin.broccolium.modules.storage.item.api.SlottedAgnosticItemStorage
import java.util.function.Supplier

class SuppliedInventoryPlugin(private val levelSup: Supplier<Level>, private val storageSup: Supplier<SlottedAgnosticItemStorage>) : AbstractInventoryPlugin() {
    override val storage: SlottedAgnosticItemStorage
        get() = storageSup.get()
    override val level: Level
        get() = levelSup.get()
}
