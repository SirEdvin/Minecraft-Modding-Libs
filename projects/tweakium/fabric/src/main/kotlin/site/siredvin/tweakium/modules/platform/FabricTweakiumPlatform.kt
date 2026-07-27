package site.siredvin.tweakium.modules.platform

import site.siredvin.tweakium.TweakiumCore

object FabricTweakiumPlatform : FabricInnerComputerBasePlatform() {
    override val modID: String
        get() = TweakiumCore.MOD_ID
}
