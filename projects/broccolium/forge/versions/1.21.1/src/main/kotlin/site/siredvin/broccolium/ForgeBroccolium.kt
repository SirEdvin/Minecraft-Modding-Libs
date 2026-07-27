package site.siredvin.broccolium

import net.neoforged.fml.common.Mod
import site.siredvin.broccolium.modules.platform.ForgeInnerPlatformToolkit
import site.siredvin.broccolium.modules.platform.ForgePlatformIngredients
import site.siredvin.broccolium.modules.platform.ForgePlatformTags
import site.siredvin.broccolium.modules.storage.ForgeStorageUtils
import site.siredvin.broccolium.modules.storage.energy.AgnosticEnergyStorageLookup
import site.siredvin.broccolium.modules.storage.fluid.AgnosticFluidStorageLookup
import site.siredvin.broccolium.modules.storage.item.AgnosticItemStorageLookup

@Mod(BroccoliumCore.MOD_ID)
object ForgeBroccolium {

    init {
        BroccoliumCore.configure(ForgeInnerPlatformToolkit, ForgePlatformTags, ForgePlatformIngredients)
        // Register extract storages
        AgnosticItemStorageLookup.addBlockLookup(ForgeStorageUtils::extractStorageFromBlock)
        AgnosticItemStorageLookup.addEntityLookup(ForgeStorageUtils::extractStorageFromEntity)
        AgnosticItemStorageLookup.addInventoryItemLookup(ForgeStorageUtils::extractItemStorageFromItem)
        AgnosticFluidStorageLookup.addBlockLookup(ForgeStorageUtils::extractFluidStorageFromBlock)
        AgnosticFluidStorageLookup.addEntityLookup(ForgeStorageUtils::extractFluidStorageFromEntity)
        AgnosticFluidStorageLookup.addInventoryItemLookup(ForgeStorageUtils::extractFluidStorageFromItem)
        AgnosticEnergyStorageLookup.addBlockLookup(ForgeStorageUtils::extractEnergyStorageFromBlock)
        AgnosticEnergyStorageLookup.addEntityLookup(ForgeStorageUtils::extractEnergyStorageFromEntity)
        AgnosticEnergyStorageLookup.addInventoryItemLookup(ForgeStorageUtils::extractEnergyStorageFromItem)
    }

    fun sayHi() {
    }
}
