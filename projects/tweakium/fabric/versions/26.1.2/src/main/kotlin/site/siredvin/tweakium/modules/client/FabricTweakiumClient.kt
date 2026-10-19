package site.siredvin.tweakium.modules.client

import dan200.computercraft.api.client.FabricComputerCraftAPIClient
import net.fabricmc.api.ClientModInitializer
import site.siredvin.tweakium.modules.turtle.client.FacingBlockTurtleModeller

object FabricTweakiumClient : ClientModInitializer {
    override fun onInitializeClient() {
        FabricComputerCraftAPIClient.registerTurtleUpgradeModeller(FacingBlockTurtleModeller.ID, FacingBlockTurtleModeller.CODEC)
    }
}
