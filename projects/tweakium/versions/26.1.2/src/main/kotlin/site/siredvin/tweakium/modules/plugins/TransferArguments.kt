package site.siredvin.tweakium.modules.plugins

import dan200.computercraft.api.lua.IArguments
import dan200.computercraft.api.lua.LuaException
import java.util.Optional
import kotlin.math.floor

// Validate before CC's generated Int conversion can saturate a Lua number.
internal fun IArguments.checkedTransferInt(index: Int): Int {
    val value = getDouble(index)
    if (!value.isFinite() || value < Int.MIN_VALUE || value > Int.MAX_VALUE || floor(value) != value) {
        throw LuaException("Argument #${index + 1} must be an integer in the signed 32-bit range")
    }
    return value.toInt()
}

internal fun IArguments.optionalTransferInt(index: Int): Optional<Int> = if (index >= count() || get(index) == null) Optional.empty() else Optional.of(checkedTransferInt(index))

internal fun IArguments.transferSlotOrQuery(index: Int): Any = if (get(index) is Number) checkedTransferInt(index) else get(index) ?: throw LuaException("Missing slot or item query")
