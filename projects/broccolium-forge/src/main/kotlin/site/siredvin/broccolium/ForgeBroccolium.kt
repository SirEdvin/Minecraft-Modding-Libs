package site.siredvin.broccolium

import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import site.siredvin.broccolium.modules.platform.ForgeInnerPlatformToolkit
import site.siredvin.broccolium.modules.platform.ForgePlatformIngredients
import site.siredvin.broccolium.modules.platform.ForgePlatformTags
import site.siredvin.broccolium.modules.storage.ForgeStorageUtils
import site.siredvin.broccolium.modules.storage.energy.AgnosticEnergyStorageLookup
import site.siredvin.broccolium.modules.storage.fluid.AgnosticFluidStorageLookup
import site.siredvin.broccolium.modules.storage.item.AgnosticItemStorageLookup

@Mod(BroccoliumCore.MOD_ID)
@EventBusSubscriber(modid = BroccoliumCore.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
object ForgeBroccolium {

    init {
        BroccoliumCore.configure(ForgeInnerPlatformToolkit, ForgePlatformTags, ForgePlatformIngredients)
        // Register extract storages
        AgnosticItemStorageLookup.addItemStorageExtractor(ForgeStorageUtils::extractStorageFromBlock)
        AgnosticFluidStorageLookup.addFluidStorageExtractor(ForgeStorageUtils::extractFluidStorageFromBlock)
        AgnosticEnergyStorageLookup.addEnergyStorageExtractor(ForgeStorageUtils::extractEnergyStorageFromBlock)
        AgnosticEnergyStorageLookup.addEnergyStorageExtractor(ForgeStorageUtils::extractEnergyStorageFromItem)
        AgnosticItemStorageLookup.addItemStorageExtractor { level, entity ->
            ForgeStorageUtils.extractStorageFromEntity(level, entity)
        }
        AgnosticFluidStorageLookup.addFluidStorageExtractor(ForgeStorageUtils::extractFluidStorageFromEntity)
    }

    fun sayHi() {
    }
}
