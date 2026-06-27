package site.siredvin.broccolium

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents
import site.siredvin.broccolium.modules.base.FabricIntegrationLoader
import site.siredvin.broccolium.modules.platform.FabricPlatformIngredients
import site.siredvin.broccolium.modules.platform.FabricPlatformTags
import site.siredvin.broccolium.modules.platform.FabricPlatformToolkit
import site.siredvin.broccolium.modules.storage.FabricStorageUtils
import site.siredvin.broccolium.modules.storage.fluid.AgnosticFluidStorageLookup
import site.siredvin.broccolium.modules.storage.item.AgnosticItemStorageLookup

object FabricBroccolium : ModInitializer {

    val loader = FabricIntegrationLoader(
        FabricBroccolium::class.java.getPackage().name,
        BroccoliumCore.LOGGER,
    )

    init {
        BroccoliumCore.configure(FabricPlatformToolkit, FabricPlatformTags, FabricPlatformIngredients)
        // Register extract storages
        AgnosticItemStorageLookup.addBlockLookup(FabricStorageUtils::extractStorage)
        AgnosticFluidStorageLookup.addBlockLookup(FabricStorageUtils::extractFluidStorage)
        AgnosticFluidStorageLookup.addInventoryItemLookup(FabricStorageUtils::extractFluidStorageFromItem)
        ServerWorldEvents.LOAD.register(
            ServerWorldEvents.Load { server, _ ->
                FabricPlatformToolkit.minecraftServer = server
            },
        )

        loader.maybeLoadIntegration("team_reborn_energy").ifPresent { (it as Runnable).run() }
    }

    fun sayHi() {}

    override fun onInitialize() {
    }
}
