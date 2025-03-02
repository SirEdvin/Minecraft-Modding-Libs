package site.siredvin.peripheralium

import site.siredvin.broccolium.modules.platform.FabricInnerBasePlatform

object FabricInnerBasePeripheraliumPlatform : FabricInnerBasePlatform() {
    override val modID: String
        get() = PeripheraliumCore.MOD_ID
}
