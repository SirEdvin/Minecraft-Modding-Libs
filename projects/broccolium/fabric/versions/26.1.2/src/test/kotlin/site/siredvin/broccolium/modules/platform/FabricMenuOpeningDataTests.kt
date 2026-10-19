package site.siredvin.broccolium.modules.platform

import io.netty.buffer.Unpooled
import net.minecraft.core.RegistryAccess
import net.minecraft.network.RegistryFriendlyByteBuf
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class FabricMenuOpeningDataTests {
    companion object {
        @JvmStatic
        fun malformedFrames(): Stream<ByteArray> = Stream.of(
            byteArrayOf(), // Missing length.
            byteArrayOf(0x80.toByte()), // Truncated length varint.
            ByteArray(5) { 0x80.toByte() }, // Varint exceeds the five-byte limit.
            byteArrayOf(0xff.toByte(), 0xff.toByte(), 0xff.toByte(), 0xff.toByte(), 0x0f), // -1.
            byteArrayOf(0xd9.toByte(), 0xfe.toByte(), 0x01), // 32601, before any payload allocation.
            byteArrayOf(0x04, 0x01), // Four declared bytes, only one supplied.
            byteArrayOf(0xd8.toByte(), 0xfe.toByte(), 0x01), // 32600, missing the whole payload.
            byteArrayOf(0xff.toByte(), 0xff.toByte(), 0xff.toByte(), 0xff.toByte(), 0x07), // Int.MAX_VALUE.
        )
    }

    private fun buffer(): RegistryFriendlyByteBuf = RegistryFriendlyByteBuf(Unpooled.buffer(), RegistryAccess.EMPTY)

    @ParameterizedTest(name = "malformed frame {index}")
    @MethodSource("malformedFrames")
    fun malformedFramesAreRejectedWithoutWritingOrReleasingCallerBuffer(frame: ByteArray) {
        val original = frame.copyOf()
        val buffer = RegistryFriendlyByteBuf(Unpooled.wrappedBuffer(frame), RegistryAccess.EMPTY)
        try {
            assertThrows(RuntimeException::class.java) { FabricMenuOpeningData.codec.decode(buffer) }
            assertEquals(frame.size, buffer.writerIndex())
            assertEquals(1, buffer.refCnt())
            assertArrayEquals(original, frame)
        } finally {
            buffer.release()
        }
    }

    @Test
    fun everyIncompletePrefixOfValidFrameIsRejected() {
        val buffer = buffer()
        try {
            FabricMenuOpeningData.codec.encode(buffer, ByteArray(256) { it.toByte() })
            val complete = ByteArray(buffer.readableBytes()).also { buffer.getBytes(0, it) }
            for (length in complete.indices) {
                val truncated = RegistryFriendlyByteBuf(Unpooled.wrappedBuffer(complete.copyOf(length)), RegistryAccess.EMPTY)
                try {
                    assertThrows(RuntimeException::class.java, { FabricMenuOpeningData.codec.decode(truncated) }, "prefix length $length")
                    assertEquals(1, truncated.refCnt())
                } finally {
                    truncated.release()
                }
            }
        } finally {
            buffer.release()
        }
    }

    @Test
    fun decodedBytesAreDetachedAndConsumeOnlyTheirFrame() {
        val buffer = buffer()
        try {
            FabricMenuOpeningData.codec.encode(buffer, byteArrayOf(1, 2, 3))
            buffer.writeInt(42)
            val decoded = FabricMenuOpeningData.codec.decode(buffer)
            assertEquals(42, buffer.readInt())
            assertEquals(0, buffer.readableBytes())
            buffer.setByte(1, 99)
            assertArrayEquals(byteArrayOf(1, 2, 3), decoded)
            decoded[1] = 88
            assertEquals(2, buffer.getByte(2).toInt())
            assertEquals(1, buffer.refCnt())
        } finally {
            buffer.release()
        }
    }

    @Test
    fun emptyDataIsAValidFrame() {
        val buffer = buffer()
        try {
            FabricMenuOpeningData.codec.encode(buffer, byteArrayOf())
            assertEquals(1, buffer.writerIndex())
            assertArrayEquals(byteArrayOf(), FabricMenuOpeningData.codec.decode(buffer))
            assertEquals(0, buffer.readableBytes())
        } finally {
            buffer.release()
        }
    }

    @Test
    fun oversizedEncodePreservesExistingFrame() {
        val buffer = buffer()
        try {
            buffer.writeInt(42)
            buffer.readerIndex(1)
            assertThrows(IllegalArgumentException::class.java) { FabricMenuOpeningData.codec.encode(buffer, ByteArray(FabricMenuOpeningData.MAX_BYTES + 1)) }
            assertEquals(1, buffer.readerIndex())
            assertEquals(4, buffer.writerIndex())
            assertEquals(42, buffer.getInt(0))
            assertEquals(1, buffer.refCnt())
        } finally {
            buffer.release()
        }
    }

    @Test
    fun savingFunctionCanFillLimitButCannotOverflowAndAlwaysReleasesBuffer() {
        var saved: RegistryFriendlyByteBuf? = null
        val data = ByteArray(FabricMenuOpeningData.MAX_BYTES) { it.toByte() }
        assertArrayEquals(
            data,
            FabricMenuOpeningData.encode(RegistryAccess.EMPTY) { output ->
                saved = output as RegistryFriendlyByteBuf
                assertSame(RegistryAccess.EMPTY, saved!!.registryAccess())
                output.writeBytes(data)
            },
        )
        assertEquals(0, saved!!.refCnt())
        assertThrows(IndexOutOfBoundsException::class.java) {
            FabricMenuOpeningData.encode(RegistryAccess.EMPTY) { output ->
                saved = output as RegistryFriendlyByteBuf
                output.writeBytes(data)
                output.writeByte(1)
            }
        }
        assertEquals(0, saved.refCnt())
    }

    @Test
    fun savingFunctionResultIsDetachedFromItsSourceArray() {
        val data = byteArrayOf(1, 2, 3)
        val encoded = FabricMenuOpeningData.encode(RegistryAccess.EMPTY) { it.writeBytes(data) }
        data[0] = 99
        assertArrayEquals(byteArrayOf(1, 2, 3), encoded)
    }

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
