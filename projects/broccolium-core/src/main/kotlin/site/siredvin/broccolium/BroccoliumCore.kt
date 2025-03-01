package site.siredvin.broccolium

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import site.siredvin.broccolium.modules.platform.PlatformIngredients
import site.siredvin.broccolium.modules.platform.PlatformTags
import site.siredvin.broccolium.modules.platform.PlatformToolkit
import site.siredvin.broccolium.modules.platform.api.InnerPlatformIngredients
import site.siredvin.broccolium.modules.platform.api.InnerPlatformTags
import site.siredvin.broccolium.modules.platform.api.InnerPlatformToolkit

object BroccoliumCore {
    const val MOD_ID = "broccolium"

    val LOGGER: Logger = LogManager.getLogger(MOD_ID)

    fun configure(platform: InnerPlatformToolkit, tags: InnerPlatformTags, ingredients: InnerPlatformIngredients) {
        PlatformToolkit.configure(platform)
        PlatformTags.configure(tags)
        PlatformIngredients.configure(ingredients)
    }
}
