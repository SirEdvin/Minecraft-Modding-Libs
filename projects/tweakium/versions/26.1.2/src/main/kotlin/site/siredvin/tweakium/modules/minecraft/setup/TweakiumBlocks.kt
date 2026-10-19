package site.siredvin.tweakium.modules.minecraft.setup

import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import site.siredvin.broccolium.modules.base.block.GenericBlockEntityBlock
import site.siredvin.broccolium.modules.base.util.BlockUtil
import site.siredvin.tweakium.TweakiumCore
import site.siredvin.tweakium.modules.minecraft.xplat.TweakiumPlatform

object TweakiumBlocks {
    val CREATIVE_FILLER = TweakiumPlatform.registerBlock(
        "creative_filler",
        {
            GenericBlockEntityBlock(
                { TweakiumBlockEntityTypes.CREATIVE_FILLER },
                false,
                properties = BlockUtil.defaultProperties().setId(
                    ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(TweakiumCore.MOD_ID, "creative_filler")),
                ),
            )
        },
    )
    fun doSomething() {}
}
