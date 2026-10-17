package site.siredvin.broccolium.modules.base.item

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.contents.TranslatableContents
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.TooltipDisplay
import net.minecraft.world.level.block.Block
import site.siredvin.broccolium.modules.base.util.itemTooltip
import java.util.function.Consumer

open class DescriptiveBlockItem(block: Block, properties: Properties) : BlockItem(block, properties) {

    private val extraDescription: MutableComponent by lazy {
        return@lazy itemTooltip(this.descriptionId)
    }

    // Preserve the extension facade and vanilla block tooltip contribution.
    @Suppress("OVERRIDE_DEPRECATION", "DEPRECATION")
    override fun appendHoverText(
        itemStack: ItemStack,
        context: TooltipContext,
        display: TooltipDisplay,
        builder: Consumer<Component>,
        tooltipFlag: TooltipFlag,
    ) {
        super.appendHoverText(itemStack, context, display, builder, tooltipFlag)
        val keyContents = extraDescription.contents as TranslatableContents
        if (keyContents.key != extraDescription.string) {
            builder.accept(extraDescription)
        }
    }
}
