package site.siredvin.tweakium.modules.platform.api

import dan200.computercraft.api.pocket.IPocketUpgrade
import dan200.computercraft.api.turtle.ITurtleUpgrade
import net.minecraft.resources.ResourceLocation
import site.siredvin.broccolium.modules.platform.api.InnerBasePlatform
import java.util.function.Supplier

interface InnerComputerBasePlatform : InnerBasePlatform {
    fun <V : ITurtleUpgrade> registerTurtleUpgrade(
        key: ResourceLocation,
        upgrade: V,
    ): Supplier<V>
    fun <V : IPocketUpgrade> registerPocketUpgrade(
        key: ResourceLocation,
        upgrade: V,
    ): Supplier<V>
}
