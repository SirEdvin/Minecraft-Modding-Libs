package site.siredvin.tweakium.modules.peripheral.util

import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.core.computer.ComputerSide
import net.minecraft.core.Direction

object OrientationUtil {
    @Throws(LuaException::class)
    fun getDirection(facing: Direction, computerSide: String): Direction {
        if (Direction.byName(computerSide) != null) return Direction.byName(computerSide)!!
        val side = ComputerSide.valueOfInsensitive(computerSide)
        if (side == ComputerSide.FRONT) return facing
        if (side == ComputerSide.BACK) return facing.opposite
        if (side == ComputerSide.TOP) return Direction.UP
        if (side == ComputerSide.BOTTOM) return Direction.DOWN
        if (side == ComputerSide.RIGHT) return facing.counterClockWise
        if (side == ComputerSide.LEFT) return facing.clockWise
        throw LuaException("$computerSide is not a valid side")
    }
}
