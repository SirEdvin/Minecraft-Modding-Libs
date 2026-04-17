package site.siredvin.tweakium.modules.minecraft.setup

import site.siredvin.broccolium.modules.base.block.GenericBlockEntityBlock
import site.siredvin.tweakium.modules.minecraft.xplat.TweakiumPlatform

object TweakiumBlocks {
    val CREATIVE_FILLER = TweakiumPlatform.registerBlock(
        "creative_filler",
        { GenericBlockEntityBlock({ TweakiumBlockEntityTypes.CREATIVE_FILLER.get() }, false) },
    )
    fun doSomething() {}
}/m
