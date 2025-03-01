package site.siredvin.broccolium.modules.data.api

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent

interface TextRecord {
    val textID: String
    val text: MutableComponent
        get() = Component.translatable(textID)
    fun format(vararg args: Any): MutableComponent = Component.translatable(textID, *args)
}
