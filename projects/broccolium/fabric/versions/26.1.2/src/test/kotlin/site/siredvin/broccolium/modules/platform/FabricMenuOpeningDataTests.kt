package site.siredvin.broccolium.modules.platform

import io.netty.buffer.Unpooled
import net.minecraft.core.RegistryAccess
import net.minecraft.network.RegistryFriendlyByteBuf
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class FabricMenuOpeningDataTests {
    private fun buffer(): RegistryFriendlyByteBuf = RegistryFriendlyByteBuf(Unpooled.buffer(), RegistryAccess.EMPTY)

    @Test
    fun codecRoundTripsAtLimitWithoutMutatingInput() {
        val data = ByteArray(FabricMenuOpeningData.MAX_BYTES) { it.toByte() }
        val original = data.copyOf()
        val buffer = buffer()
        try {
            FabricMenuOpeningData.codec.encode(buffer, data)
            assertArrayEquals(original, data)
            assertArrayEquals(data, FabricMenuOpeningData.codec.decode(buffer))
            assertEquals(0, buffer.readableBytes())
        } finally {
            buffer.release()
        }
    }

    @Test
    fun oversizedEncodeFailsBeforeWriting() {
        val buffer = buffer()
        try {
            assertThrows(IllegalArgumentException::class.java) { FabricMenuOpeningData.codec.encode(buffer, ByteArray(FabricMenuOpeningData.MAX_BYTES + 1)) }
            assertEquals(0, buffer.writerIndex())
        } finally {
            buffer.release()
        }
    }

    @Test
    fun oversizedAndTruncatedLengthsAreRejected() {
        val buffer = buffer()
        try {
            buffer.writeVarInt(FabricMenuOpeningData.MAX_BYTES + 1)
            assertThrows(RuntimeException::class.java) { FabricMenuOpeningData.codec.decode(buffer) }
            buffer.clear()
            buffer.writeVarInt(4).writeByte(1)
            assertThrows(RuntimeException::class.java) { FabricMenuOpeningData.codec.decode(buffer) }
            buffer.clear()
            buffer.writeVarInt(-1)
            assertThrows(RuntimeException::class.java) { FabricMenuOpeningData.codec.decode(buffer) }
        } finally {
            buffer.release()
        }
    }

    @Test
    fun encoderReleasesTemporaryBufferAfterSuccessAndFailure() {
        var saved: RegistryFriendlyByteBuf? = null
        val data = FabricMenuOpeningData.encode(RegistryAccess.EMPTY) { output ->
            saved = output as RegistryFriendlyByteBuf
            output.writeInt(42)
        }
        assertEquals(0, saved!!.refCnt())
        assertEquals(4, data.size)
        assertThrows(IllegalStateException::class.java) {
            FabricMenuOpeningData.encode(RegistryAccess.EMPTY) { output ->
                saved = output as RegistryFriendlyByteBuf
                throw IllegalStateException("fixture writer failure")
            }
        }
        assertEquals(0, saved.refCnt())
    }
}
