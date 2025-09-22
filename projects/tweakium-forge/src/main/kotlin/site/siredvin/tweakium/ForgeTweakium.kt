package site.siredvin.tweakium

import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import site.siredvin.broccolium.ForgeBroccolium
import site.siredvin.tweakium.modules.minecraft.xplat.TweakiumCommonHooks
import site.siredvin.tweakium.modules.platform.ForgeComputerPlatformToolkit
import site.siredvin.tweakium.modules.platform.ForgeTweakiumPlatform
import thedarkcolour.kotlinforforge.forge.MOD_CONTEXT

@Mod(TweakiumCore.MOD_ID)
@EventBusSubscriber(modid = TweakiumCore.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
object ForgeTweakium {

    val blocksRegistry: DeferredRegister<Block> =
        DeferredRegister.create(ForgeRegistries.BLOCKS, TweakiumCore.MOD_ID)
    val itemsRegistry: DeferredRegister<Item> =
        DeferredRegister.create(ForgeRegistries.ITEMS, TweakiumCore.MOD_ID)
    val blockEntityTypesRegistry: DeferredRegister<BlockEntityType<*>> =
        DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, TweakiumCore.MOD_ID)

    init {
        TweakiumCore.configure(ForgeComputerPlatformToolkit, ForgeTweakiumPlatform)
        val eventBus = MOD_CONTEXT.getKEventBus()
        TweakiumCommonHooks.onRegister()
        blocksRegistry.register(eventBus)
        blockEntityTypesRegistry.register(eventBus)
        itemsRegistry.register(eventBus)
    }

    fun sayHi() {
        ForgeBroccolium.sayHi()
    }
}
