package site.siredvin.tweakium

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import site.siredvin.broccolium.modules.storage.energy.AgnosticEnergyStorageLookup
import site.siredvin.tweakium.modules.minecraft.xplat.TweakiumPlatform
import site.siredvin.tweakium.modules.platform.ComputerPlatformToolkit
import site.siredvin.tweakium.modules.platform.api.InnerComputerBasePlatform
import site.siredvin.tweakium.modules.platform.api.InnerComputerPlatformToolkit
import site.siredvin.tweakium.modules.storage.energy.TurtleAgnosticEnergyStorage

object TweakiumCore {
    const val MOD_ID = "tweakium"

    val LOGGER: Logger = LogManager.getLogger(MOD_ID)

    fun configure(computerPlatform: InnerComputerPlatformToolkit, basePlatform: InnerComputerBasePlatform) {
        ComputerPlatformToolkit.configure(computerPlatform)
        TweakiumPlatform.configure(basePlatform)
        AgnosticEnergyStorageLookup.addBlockLookup { _, _, blockEntity, _ ->
            if (blockEntity != null) {
                val turtle = ComputerPlatformToolkit.get().getTurtleAccess(blockEntity)
                if (turtle != null) return@addBlockLookup TurtleAgnosticEnergyStorage(turtle)
            }
            return@addBlockLookup null
        }
        ComputerPlatformToolkit.get().registerGenericPeripheralLookup()
    }
}
