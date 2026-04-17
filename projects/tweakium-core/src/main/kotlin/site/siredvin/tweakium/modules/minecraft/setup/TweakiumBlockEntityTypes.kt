package site.siredvin.tweakium.modules.minecraft.setup

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.entity.BlockEntityType
import site.siredvin.broccolium.modules.platform.PlatformToolkit
import site.siredvin.tweakium.TweakiumCore
import site.siredvin.tweakium.modules.minecraft.blockentity.CreativeFillerBlockEntity
import site.siredvin.tweakium.modules.minecraft.xplat.TweakiumPlatform
import java.util.function.Supplier

object TweakiumBlockEntityTypes {
    val CREATIVE_FILLER: Supplier<BlockEntityType<CreativeFillerBlockEntity>> = TweakiumPlatform.registerBlockEntity(
        ResourceLocation.fromNamespaceAndPath(TweakiumCore.MOD_ID, "creative_filler"),
    ) {
        PlatformToolkit.get().createBlockEntityType(
            ::CreativeFillerBlockEntity,
            TweakiumBlocks.CREATIVE_FILLER.get(),
        )
    }
    fun doSomething() {}
}
