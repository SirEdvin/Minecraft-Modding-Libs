package site.siredvin.broccolium.test

import com.google.gson.JsonPrimitive
import com.mojang.serialization.JsonOps
import org.junit.jupiter.api.Test
import site.siredvin.broccolium.modules.base.codec.BlockCodec
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TargetCodecTests {
    @Test
    fun enabledBlockFlagsSurviveCodecRoundTrip() {
        val encoded = BlockCodec.BOOLEAN_CODEC.encodeStart(JsonOps.INSTANCE, true).getOrThrow()
        assertEquals(JsonPrimitive(true), encoded)
        assertEquals(true, BlockCodec.BOOLEAN_CODEC.parse(JsonOps.INSTANCE, encoded).getOrThrow())
    }

    @Test
    fun disabledBlockFlagsSurviveCodecRoundTrip() {
        val encoded = BlockCodec.BOOLEAN_CODEC.encodeStart(JsonOps.INSTANCE, false).getOrThrow()
        assertEquals(JsonPrimitive(false), encoded)
        assertEquals(false, BlockCodec.BOOLEAN_CODEC.parse(JsonOps.INSTANCE, encoded).getOrThrow())
    }

    @Test
    fun malformedBlockFlagsAreRejectedInsteadOfBecomingFalse() {
        assertTrue(BlockCodec.BOOLEAN_CODEC.parse(JsonOps.INSTANCE, JsonPrimitive("invalid")).error().isPresent)
    }
}
