package site.siredvin.tweakium.modules

import net.fabricmc.api.ModInitializer
import site.siredvin.broccolium.FabricBroccolium
import site.siredvin.tweakium.TweakiumCore
import site.siredvin.tweakium.modules.minecraft.xplat.TweakiumCommonHooks
import site.siredvin.tweakium.modules.platform.FabricComputerPlatformToolkit
import site.siredvin.tweakium.modules.platform.FabricTweakiumPlatform

object FabricTweakium : ModInitializer {

    init {
        TweakiumCore.configure(FabricComputerPlatformToolkit, FabricTweakiumPlatform)
    }

    fun sayHi() {
        FabricBroccolium.sayHi()
    }

    override fun onInitialize() {
        FabricBroccolium.sayHi()
        TweakiumCommonHooks.onRegister()
    }
}
