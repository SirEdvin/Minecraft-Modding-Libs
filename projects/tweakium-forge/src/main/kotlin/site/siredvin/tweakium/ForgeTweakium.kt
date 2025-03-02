package site.siredvin.tweakium

import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import site.siredvin.broccolium.ForgeBroccolium
import site.siredvin.tweakium.modules.platform.ForgeComputerPlatformToolkit

@Mod(TweakiumCore.MOD_ID)
@EventBusSubscriber(modid = TweakiumCore.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
object ForgeTweakium {

    init {
        TweakiumCore.configure(ForgeComputerPlatformToolkit)
    }

    fun sayHi() {
        ForgeBroccolium.sayHi()
    }
}
