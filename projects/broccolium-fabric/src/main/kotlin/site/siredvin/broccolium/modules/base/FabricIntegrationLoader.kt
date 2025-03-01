package site.siredvin.broccolium.modules.base

import net.fabricmc.loader.api.FabricLoader
import org.apache.logging.log4j.Logger

class FabricIntegrationLoader(corePackage: String, logger: Logger) : BaseIntegrationLoader(corePackage, logger) {
    override fun isModPresent(modID: String): Boolean = FabricLoader.getInstance().allMods.stream().anyMatch { it.metadata.id == modID }
}
