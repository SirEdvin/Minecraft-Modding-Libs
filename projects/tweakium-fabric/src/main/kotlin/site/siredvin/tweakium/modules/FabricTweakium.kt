package site.siredvin.tweakium.modules

import net.fabricmc.api.ModInitializer
import site.siredvin.broccolium.FabricBroccolium
import site.siredvin.tweakium.TweakiumCore
import site.siredvin.tweakium.modules.platform.FabricComputerPlatformToolkit

object FabricTweakium : ModInitializer {

    init {
        TweakiumCore.configure(FabricComputerPlatformToolkit)
    }

    fun sayHi() {
        FabricBroccolium.sayHi()
    }

    override fun onInitialize() {
    }
}
