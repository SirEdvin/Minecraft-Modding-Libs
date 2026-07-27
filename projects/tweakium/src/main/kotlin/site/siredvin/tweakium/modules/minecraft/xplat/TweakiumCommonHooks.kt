package site.siredvin.tweakium.modules.minecraft.xplat

import site.siredvin.tweakium.modules.minecraft.setup.TweakiumBlockEntityTypes
import site.siredvin.tweakium.modules.minecraft.setup.TweakiumBlocks

object TweakiumCommonHooks {

    fun onRegister() {
        TweakiumBlocks.doSomething()
        TweakiumBlockEntityTypes.doSomething()
    }
}
