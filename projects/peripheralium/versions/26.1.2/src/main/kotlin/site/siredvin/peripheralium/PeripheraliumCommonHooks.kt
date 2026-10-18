package site.siredvin.peripheralium

import net.minecraft.resources.Identifier
import site.siredvin.broccolium.modules.platform.PlatformToolkit

object PeripheraliumCommonHooks {
    fun onRegister() {
        Blocks.doSomething()
        Items.doSomething()
        PeripheraliumPlatform.registerCreativeTab(
            Identifier.fromNamespaceAndPath(PeripheraliumCore.MOD_ID, "tab"),
            PeripheraliumCore.configureCreativeTab(PlatformToolkit.get().createTabBuilder()).build(),
        )
    }
}
