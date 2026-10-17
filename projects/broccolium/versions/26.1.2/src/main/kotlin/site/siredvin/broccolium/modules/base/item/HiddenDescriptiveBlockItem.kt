package site.siredvin.broccolium.modules.base.item

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.TooltipDisplay
import net.minecraft.world.level.block.Block
import site.siredvin.broccolium.modules.data.BroccoliumText
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Supplier

open class HiddenDescriptiveBlockItem(block: Block, properties: Properties, private var enableSup: Supplier<Boolean>, private var alwaysShow: Boolean = false, private vararg val tooltipHook: Function<HiddenDescriptiveBlockItem, List<Component>>) : DescriptiveBlockItem(block, properties) {

    private val tooltips: List<Component> by lazy {
        return@lazy tooltipHook.flatMap { it.apply(this) }
    }

    override fun appendHoverText(
        itemStack: ItemStack,
        context: TooltipContext,
        display: TooltipDisplay,
        builder: Consumer<Component>,
        tooltipFlag: TooltipFlag,
    ) {
        super.appendHoverText(itemStack, context, display, builder, tooltipFlag)
        if (alwaysShow || InputConstants.isKeyDown(Minecraft.getInstance().window, InputConstants.KEY_LSHIFT)) {
            tooltips.forEach(builder::accept)
        } else {
            builder.accept(BroccoliumText.PRESS_FOR_DESCRIPTION.text)
        }
    }

    fun isEnabled(): Boolean = enableSup.get()
}
