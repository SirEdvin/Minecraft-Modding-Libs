package site.siredvin.peripheralium

import net.fabricmc.api.ModInitializer
import site.siredvin.broccolium.FabricBroccolium
import site.siredvin.tweakium.modules.FabricTweakium

object FabricPeripheralium : ModInitializer {

    init {
        PeripheraliumCore.configure(FabricInnerBasePeripheraliumPlatform)
    }

    fun sayHi() {
        FabricBroccolium.sayHi()
        FabricTweakium.sayHi()
    }

    override fun onInitialize() {
        sayHi()
        PeripheraliumCommonHooks.onRegister()
    }
}
