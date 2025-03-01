package site.siredvin.tweakium

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import site.siredvin.broccolium.modules.storage.energy.AgnosticEnergyStorageLookup
import site.siredvin.broccolium.modules.storage.energy.api.AgnosticEnergyStorageExtractor
import site.siredvin.tweakium.modules.platform.ComputerPlatformToolkit
import site.siredvin.tweakium.modules.storage.energy.TurtleAgnosticEnergyStorage

object TweakiumCore {
    const val MOD_ID = "tweakium"

    val LOGGER: Logger = LogManager.getLogger(MOD_ID)

    fun configure(libPlatform: BaseInnerPlatform, platform: PeripheraliumPlatform, ingredients: RecipeIngredients, tags: XplatTags) {
        LibPlatform.configure(libPlatform)
        PeripheraliumPlatform.configure(platform)
        RecipeIngredients.configure(ingredients)
        XplatTags.configure(tags)
        AgnosticEnergyStorageLookup.addEnergyStorageExtractor(AgnosticEnergyStorageExtractor { _, _, blockEntity ->
            if (blockEntity != null) {
                val turtle = ComputerPlatformToolkit.get().getTurtleAccess(blockEntity)
                if (turtle != null) return@AgnosticEnergyStorageExtractor TurtleAgnosticEnergyStorage(turtle)
            }
            return@AgnosticEnergyStorageExtractor null
        })

    }
}
