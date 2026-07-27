package site.siredvin.tweakium.modules.pocket.api

import dan200.computercraft.api.pocket.IPocketAccess
import dan200.computercraft.api.pocket.IPocketUpgrade
import dan200.computercraft.api.upgrades.UpgradeData

interface PocketUpgradeHolder {
    fun getInternalUpgrades(pocket: IPocketAccess): List<UpgradeData<IPocketUpgrade>>
}
