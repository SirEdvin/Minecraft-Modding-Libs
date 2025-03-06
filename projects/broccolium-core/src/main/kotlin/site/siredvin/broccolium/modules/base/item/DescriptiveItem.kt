package site.siredvin.broccolium.modules.base.item

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.contents.TranslatableContents
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import site.siredvin.broccolium.modules.base.util.itemTooltip

open class DescriptiveItem(properties: Properties) : Item(properties) {
    private val extraDescription: MutableComponent by lazy {
        return@lazy itemTooltip(this.descriptionId)
    }
    override fun appendHoverText(
        itemStack: ItemStack,
        context: TooltipContext,
        list: MutableList<Component>,
        tooltipFlag: TooltipFlag,
    ) {
        val keyContents = extraDescription.contents as TranslatableContents
        if (keyContents.key != extraDescription.string) {
            list.add(extraDescription)
        }
    }
}
