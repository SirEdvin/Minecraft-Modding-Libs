package site.siredvin.peripheralium

import net.minecraft.world.item.CreativeModeTab
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import site.siredvin.broccolium.modules.platform.api.InnerBasePlatform
import site.siredvin.peripheralium.data.PeripheraliumText

object PeripheraliumCore {
    const val MOD_ID = "peripheralium"

    val LOGGER: Logger = LogManager.getLogger(MOD_ID)

    fun configureCreativeTab(builder: CreativeModeTab.Builder): CreativeModeTab.Builder = builder.icon { Items.PERIPHERALIUM_DUST.get().defaultInstance }
        .title(PeripheraliumText.CREATIVE_TAB.text)
        .displayItems { _, output ->
            PeripheraliumPlatform.holder.items.forEach {
                output.accept(it.get())
            }
            PeripheraliumPlatform.holder.blocks.forEach {
                output.accept(it.get())
            }
        }

    fun configure(platform: InnerBasePlatform) {
        PeripheraliumPlatform.configure(platform)
    }
}
