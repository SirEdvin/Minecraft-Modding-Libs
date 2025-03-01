package site.siredvin.broccolium.modules.platform.api

import net.minecraft.network.FriendlyByteBuf

fun interface SavingFunction {
    fun toBytes(buf: FriendlyByteBuf)
}
