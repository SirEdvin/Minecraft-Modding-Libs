package site.siredvin.peripheralium

import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.neoforged.neoforge.registries.DeferredRegister
import site.siredvin.broccolium.modules.platform.ForgeInnerBasePlatform

object ForgePeripheraliumPlatform : ForgeInnerBasePlatform() {
    override val modID: String
        get() = PeripheraliumCore.MOD_ID

    override val blocksRegistry: DeferredRegister<Block>
        get() = ForgePeripheralium.blocksRegistry

    override val itemsRegistry: DeferredRegister<Item>
        get() = ForgePeripheralium.itemsRegistry

    override val creativeTabRegistry: DeferredRegister<CreativeModeTab>
        get() = ForgePeripheralium.creativeTabRegistry
}
