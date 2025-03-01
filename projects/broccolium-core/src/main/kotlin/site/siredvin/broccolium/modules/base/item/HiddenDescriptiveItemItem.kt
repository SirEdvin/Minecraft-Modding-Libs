package site.siredvin.broccolium.modules.base.item

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import site.siredvin.broccolium.modules.data.BroccoliumText
import java.util.function.Function
import java.util.function.Supplier

open class HiddenDescriptiveItemItem(properties: Properties, private var enableSup: Supplier<Boolean>, private var alwaysShow: Boolean = false, private vararg val tooltipHook: Function<HiddenDescriptiveItemItem, List<Component>>) : DescriptiveItem(properties) {
    private val tooltips: List<Component> by lazy {
        return@lazy tooltipHook.flatMap { it.apply(this) }
    }

    override fun appendHoverText(
        itemStack: ItemStack,
        level: Level?,
        list: MutableList<Component>,
        tooltipFlag: TooltipFlag,
    ) {
        super.appendHoverText(itemStack, level, list, tooltipFlag)
        if (alwaysShow || InputConstants.isKeyDown(Minecraft.getInstance().window.window, InputConstants.KEY_LSHIFT)) {
            list.addAll(tooltips)
        } else {
            list.add(BroccoliumText.PRESS_FOR_DESCRIPTION.text)
        }
    }

    fun isEnabled(): Boolean = enableSup.get()
}
