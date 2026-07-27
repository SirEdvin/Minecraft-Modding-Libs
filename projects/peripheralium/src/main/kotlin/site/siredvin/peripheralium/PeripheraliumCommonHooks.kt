package site.siredvin.peripheralium

import net.minecraft.resources.ResourceLocation
import site.siredvin.broccolium.modules.platform.PlatformToolkit

object PeripheraliumCommonHooks {

    fun onRegister() {
        Blocks.doSomething()
        Items.doSomething()
        PeripheraliumPlatform.registerCreativeTab(
            ResourceLocation(PeripheraliumCore.MOD_ID, "tab"),
            PeripheraliumCore.configureCreativeTab(PlatformToolkit.get().createTabBuilder()).build(),
        )
    }
}
