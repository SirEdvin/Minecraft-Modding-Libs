package site.siredvin.tweakium.modules.client

import dan200.computercraft.api.client.turtle.RegisterTurtleModelEvent
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import site.siredvin.tweakium.TweakiumCore
import site.siredvin.tweakium.modules.turtle.client.FacingBlockTurtleModeller

@EventBusSubscriber(modid = TweakiumCore.MOD_ID, value = [Dist.CLIENT])
object ForgeTweakiumClient {
    @SubscribeEvent
    fun registerModels(event: RegisterTurtleModelEvent) {
        event.register(FacingBlockTurtleModeller.ID, FacingBlockTurtleModeller.CODEC)
    }
}
