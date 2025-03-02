package site.siredvin.tweakium.modules.platform

import dan200.computercraft.api.pocket.IPocketUpgrade
import dan200.computercraft.api.pocket.PocketUpgradeSerialiser
import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.registries.DeferredRegister
import site.siredvin.broccolium.modules.platform.ForgeInnerBasePlatform
import site.siredvin.tweakium.modules.platform.api.InnerComputerBasePlatform
import java.util.function.Supplier

abstract class ForgeBaseInnerPlatform :
    ForgeInnerBasePlatform(),
    InnerComputerBasePlatform {
    open val turtleSerializers: DeferredRegister<TurtleUpgradeSerialiser<*>>?
        get() = null
    open val pocketSerializers: DeferredRegister<PocketUpgradeSerialiser<*>>?
        get() = null

    override fun <V : ITurtleUpgrade> registerTurtleUpgrade(
        key: ResourceLocation,
        serializer: TurtleUpgradeSerialiser<V>,
    ): Supplier<TurtleUpgradeSerialiser<V>> = turtleSerializers!!.register(key.path) { serializer }

    override fun <V : IPocketUpgrade> registerPocketUpgrade(
        key: ResourceLocation,
        serializer: PocketUpgradeSerialiser<V>,
    ): Supplier<PocketUpgradeSerialiser<V>> = pocketSerializers!!.register(key.path) { serializer }
}
