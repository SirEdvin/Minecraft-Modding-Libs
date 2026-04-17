package site.siredvin.peripheralium

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.registries.DeferredRegister
import site.siredvin.tweakium.ForgeTweakium
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_CONTEXT

@Mod(PeripheraliumCore.MOD_ID)
@EventBusSubscriber(modid = PeripheraliumCore.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
object ForgePeripheralium {

    val blocksRegistry: DeferredRegister<Block> =
        DeferredRegister.create(BuiltInRegistries.BLOCK, PeripheraliumCore.MOD_ID)
    val itemsRegistry: DeferredRegister<Item> =
        DeferredRegister.create(BuiltInRegistries.ITEM, PeripheraliumCore.MOD_ID)
    val creativeTabRegistry: DeferredRegister<CreativeModeTab> =
        DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), PeripheraliumCore.MOD_ID)

    init {
        sayHi()
        PeripheraliumCore.configure(ForgePeripheraliumPlatform)
        val eventBus = MOD_CONTEXT.getKEventBus()
        PeripheraliumCommonHooks.onRegister()
        blocksRegistry.register(eventBus)
        itemsRegistry.register(eventBus)
        creativeTabRegistry.register(eventBus)
    }

    fun sayHi() {
        ForgeTweakium.sayHi()
    }
}
