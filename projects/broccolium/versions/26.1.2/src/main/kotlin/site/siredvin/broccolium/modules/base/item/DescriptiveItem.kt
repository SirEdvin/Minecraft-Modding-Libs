package site.siredvin.broccolium.modules.base.item

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.contents.TranslatableContents
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.TooltipDisplay
import site.siredvin.broccolium.modules.base.util.itemTooltip
import java.util.function.Consumer

open class DescriptiveItem(properties: Properties) : Item(properties) {
    private val extraDescription: MutableComponent by lazy {
        return@lazy itemTooltip(this.descriptionId)
    }

    // Preserve the extension facade at the native tooltip callback boundary.
    @Suppress("OVERRIDE_DEPRECATION")
    override fun appendHoverText(
        itemStack: ItemStack,
        context: TooltipContext,
        display: TooltipDisplay,
        builder: Consumer<Component>,
        tooltipFlag: TooltipFlag,
    ) {
        val keyContents = extraDescription.contents as TranslatableContents
        if (keyContents.key != extraDescription.string) {
            builder.accept(extraDescription)
        }
    }
}
