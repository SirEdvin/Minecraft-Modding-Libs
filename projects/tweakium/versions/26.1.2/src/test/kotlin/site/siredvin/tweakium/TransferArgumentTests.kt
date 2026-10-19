package site.siredvin.tweakium

import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.ObjectArguments
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import site.siredvin.tweakium.modules.plugins.checkedTransferInt
import site.siredvin.tweakium.modules.plugins.optionalTransferInt

class TransferArgumentTests {
    @ParameterizedTest
    @ValueSource(doubles = [1e100, -1e100, 1.5, 2147483648.0, -2147483649.0])
    fun overflowAndFractionalValuesRejectBeforeGeneratedCoercion(value: Double) {
        assertThrows<LuaException> { ObjectArguments(value).checkedTransferInt(0) }
    }

    @Test
    fun nonfiniteValuesAreRejected() {
        for (value in listOf(Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY)) {
            assertThrows<LuaException> { ObjectArguments(value).checkedTransferInt(0) }
        }
    }

    @Test
    fun signedBoundsAndMissingOptionalValuesRetainTheirMeaning() {
        for (value in listOf(Int.MIN_VALUE, -1, 0, Int.MAX_VALUE)) assertEquals(value, ObjectArguments(value).checkedTransferInt(0))
        assertEquals(java.util.Optional.empty<Int>(), ObjectArguments().optionalTransferInt(0))
        assertEquals(java.util.Optional.empty<Int>(), ObjectArguments(null as Any?).optionalTransferInt(0))
    }
}
