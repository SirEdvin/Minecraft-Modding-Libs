package site.siredvin.tweakium.modules.plugins

import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import site.siredvin.broccolium.modules.storage.base.api.SlottedAgnosticStorage
import java.util.function.Supplier

class SuppliedInventoryPlugin(
    private val levelSup: Supplier<Level>,
    private val storageSup: Supplier<SlottedAgnosticStorage<ItemStack, Int>>,
    override val inventoryTransferLimit: Int,
) : AbstractInventoryPlugin() {
    override val storage: SlottedAgnosticStorage<ItemStack, Int>
        get() = storageSup.get()
    override val level: Level
        get() = levelSup.get()
}
