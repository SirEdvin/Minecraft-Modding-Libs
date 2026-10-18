package site.siredvin.broccolium.modules.platform

import io.netty.buffer.Unpooled
import net.minecraft.core.RegistryAccess
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.AbstractContainerMenu
import site.siredvin.broccolium.modules.platform.api.MenuBuilder
import site.siredvin.broccolium.modules.platform.api.SavingFunction

/** Detached, bounded opening data. Temporary buffers never escape the callback. */
internal object FabricMenuOpeningData {
    // Match NeoForge's maximum extra opening data for the shared menu contract.
    const val MAX_BYTES = 32600

    val codec: StreamCodec<RegistryFriendlyByteBuf, ByteArray> = object : StreamCodec<RegistryFriendlyByteBuf, ByteArray> {
        override fun decode(buffer: RegistryFriendlyByteBuf): ByteArray = buffer.readByteArray(MAX_BYTES)

        override fun encode(buffer: RegistryFriendlyByteBuf, value: ByteArray) {
            require(value.size <= MAX_BYTES) { "Menu opening data exceeds $MAX_BYTES bytes" }
            buffer.writeByteArray(value)
        }
    }

    fun encode(registries: RegistryAccess, savingFunction: SavingFunction): ByteArray {
        val buffer = RegistryFriendlyByteBuf(Unpooled.buffer(256, MAX_BYTES), registries)
        try {
            savingFunction.toBytes(buffer)
            return ByteArray(buffer.readableBytes()).also { buffer.getBytes(buffer.readerIndex(), it) }
        } finally {
            buffer.release()
        }
    }

    fun <M : AbstractContainerMenu> build(builder: MenuBuilder<M>, id: Int, inventory: Inventory, data: ByteArray): M {
        require(data.size <= MAX_BYTES) { "Menu opening data exceeds $MAX_BYTES bytes" }
        val buffer = RegistryFriendlyByteBuf(Unpooled.wrappedBuffer(data), inventory.player.registryAccess())
        try {
            return builder.build(id, inventory, buffer)
        } finally {
            buffer.release()
        }
    }
}
