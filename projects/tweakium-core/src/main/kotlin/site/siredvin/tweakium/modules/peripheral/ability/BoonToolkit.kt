package site.siredvin.tweakium.modules.peripheral.ability

import dan200.computercraft.api.turtle.ITurtleAccess
import dan200.computercraft.api.turtle.TurtleSide
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import site.siredvin.tweakium.modules.peripheral.api.IOwnedPeripheral
import site.siredvin.tweakium.modules.peripheral.api.IPeripheralOwnerBoon
import site.siredvin.tweakium.modules.platform.ComputerPlatformToolkit

object BoonToolkit {

    private fun <T : IPeripheralOwnerBoon> extractAbilityFromTurtle(turtle: ITurtleAccess, side: TurtleSide, ability: PeripheralOwnerBoonKey<T>): T? {
        val targetPeripheral = turtle.getPeripheral(side)
        if (targetPeripheral !is IOwnedPeripheral<*>) {
            return null
        }
        return targetPeripheral.peripheralOwner!!.getBoon(ability)
    }

    fun <T : IPeripheralOwnerBoon> extractAbility(ability: PeripheralOwnerBoonKey<T>, level: Level, pos: BlockPos): Pair<T?, String?> {
        val entity: BlockEntity = level.getBlockEntity(pos)
            ?: return Pair(null, "Target block doesn't posses required ability")
        val turtle = ComputerPlatformToolkit.get().getTurtleAccess(entity)
        if (turtle != null) {
            val targetAbility = extractAbilityFromTurtle(turtle, TurtleSide.LEFT, ability) ?: extractAbilityFromTurtle(turtle, TurtleSide.RIGHT, ability)
                ?: return Pair(null, "Turtle doesn't posses required ability")
            return Pair(targetAbility, null)
        }
        return Pair(null, "Target block doesn't posses required ability")
    }
}
