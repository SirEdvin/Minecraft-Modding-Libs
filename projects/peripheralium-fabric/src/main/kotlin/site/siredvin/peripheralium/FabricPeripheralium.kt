package site.siredvin.peripheralium

import net.fabricmc.api.ModInitializer
import site.siredvin.tweakium.modules.FabricTweakium

object FabricPeripheralium : ModInitializer {

    init {
        PeripheraliumCore.configure(FabricInnerBasePeripheraliumPlatform)
    }

    fun sayHi() {
        FabricTweakium.sayHi()
    }

    override fun onInitialize() {
        sayHi()
        PeripheraliumCommonHooks.onRegister()
    }
}
