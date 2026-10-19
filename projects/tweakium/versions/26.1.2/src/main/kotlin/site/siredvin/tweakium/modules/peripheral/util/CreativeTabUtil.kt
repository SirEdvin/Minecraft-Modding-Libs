package site.siredvin.tweakium.modules.peripheral.util

import dan200.computercraft.api.pocket.IPocketUpgrade
import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.api.upgrades.UpgradeData
import dan200.computercraft.shared.ModRegistry
import dan200.computercraft.shared.pocket.items.PocketComputerItem
import dan200.computercraft.shared.turtle.items.TurtleItem
import dan200.computercraft.shared.util.DataComponentUtil
import net.minecraft.core.HolderLookup
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack

object CreativeTabUtil {
    private fun addTurtle(modID: String, out: CreativeModeTab.Output, turtle: TurtleItem, registries: HolderLookup.Provider) {
        out.accept(ItemStack(turtle))
        registries.lookupOrThrow(ITurtleUpgrade.REGISTRY).listElements()
            .filter { it.key().identifier().namespace == modID }
            .map { DataComponentUtil.createStack(turtle, ModRegistry.DataComponents.RIGHT_TURTLE_UPGRADE.get(), UpgradeData.ofDefault(it)) }
            .forEach(out::accept)
    }

    private fun addPocket(modID: String, out: CreativeModeTab.Output, pocket: PocketComputerItem, registries: HolderLookup.Provider) {
        out.accept(ItemStack(pocket))
        registries.lookupOrThrow(IPocketUpgrade.REGISTRY).listElements()
            .filter { it.key().identifier().namespace == modID }
            .map { DataComponentUtil.createStack(pocket, ModRegistry.DataComponents.BACK_POCKET_UPGRADE.get(), UpgradeData.ofDefault(it)) }
            .forEach(out::accept)
    }

    fun enrichCreativeTabWithUpgrades(modID: String, output: CreativeModeTab.Output, registries: HolderLookup.Provider) {
        addPocket(modID, output, ModRegistry.Items.POCKET_COMPUTER_NORMAL.get(), registries)
        addPocket(modID, output, ModRegistry.Items.POCKET_COMPUTER_ADVANCED.get(), registries)
        addTurtle(modID, output, ModRegistry.Items.TURTLE_NORMAL.get(), registries)
        addTurtle(modID, output, ModRegistry.Items.TURTLE_ADVANCED.get(), registries)
    }
}
