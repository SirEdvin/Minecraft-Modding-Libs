package site.siredvin.tweakium

import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType
import net.neoforged.neoforge.registries.DeferredRegister
import site.siredvin.broccolium.ForgeBroccolium
import site.siredvin.tweakium.modules.minecraft.xplat.TweakiumCommonHooks
import site.siredvin.tweakium.modules.platform.ForgeComputerPlatformToolkit
import site.siredvin.tweakium.modules.platform.ForgeTweakiumPlatform
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_CONTEXT

object ForgeTweakium {

    val blocksRegistry: DeferredRegister<Block> =
        DeferredRegister.create(BuiltInRegistries.BLOCK, TweakiumCore.MOD_ID)
    val itemsRegistry: DeferredRegister<Item> =
        DeferredRegister.create(BuiltInRegistries.ITEM, TweakiumCore.MOD_ID)
    val blockEntityTypesRegistry: DeferredRegister<BlockEntityType<*>> =
        DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, TweakiumCore.MOD_ID)
    val dataComponentTypesRegistry: DeferredRegister<DataComponentType<*>> =
        DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, TweakiumCore.MOD_ID)

    init {
        TweakiumCore.configure(ForgeComputerPlatformToolkit, ForgeTweakiumPlatform)
        val eventBus = MOD_CONTEXT.getKEventBus()
        TweakiumCommonHooks.onRegister()
        blocksRegistry.register(eventBus)
        blockEntityTypesRegistry.register(eventBus)
        itemsRegistry.register(eventBus)
        dataComponentTypesRegistry.register(eventBus)
    }

    fun sayHi() {
        ForgeBroccolium.sayHi()
    }
}
