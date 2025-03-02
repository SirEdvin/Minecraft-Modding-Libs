package site.siredvin.tweakium.modules.peripheral.api

import dan200.computercraft.api.peripheral.IComputerAccess
import dan200.computercraft.api.peripheral.IPeripheral
import java.util.function.Consumer

/* This interface should be used nearly everywhere instead of IPeripheral, because this information that extract provided to peripherals */
interface IExpandedPeripheral : IPeripheral {
    fun forEachComputer(func: Consumer<IComputerAccess>)
    fun queueEvent(event: String, vararg arguments: Any) {
        forEachComputer {
            it.queueEvent(event, *arguments)
        }
    }
    fun isComputerPresent(computerID: Int): Boolean
    val connectedComputersCount: Int
}
