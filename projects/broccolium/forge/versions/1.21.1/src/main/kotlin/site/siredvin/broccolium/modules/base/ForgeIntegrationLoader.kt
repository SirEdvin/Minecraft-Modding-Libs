package site.siredvin.broccolium.modules.base

import net.neoforged.fml.ModList
import org.apache.logging.log4j.Logger

class ForgeIntegrationLoader(corePackage: String, logger: Logger) : BaseIntegrationLoader(corePackage, logger) {
    override fun isModPresent(modID: String): Boolean = ModList.get().isLoaded(modID)
}
