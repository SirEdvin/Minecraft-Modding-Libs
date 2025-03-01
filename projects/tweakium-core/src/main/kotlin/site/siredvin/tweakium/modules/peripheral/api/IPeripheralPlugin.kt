package site.siredvin.tweakium.modules.peripheral.api

import dan200.computercraft.shared.computer.core.ServerContext
import net.minecraft.server.MinecraftServer
import site.siredvin.peripheralium.computercraft.peripheral.BoundMethod

interface IPeripheralPlugin {
    var connectedPeripheral: IExpandedPeripheral?
        get() = null
        set(@Suppress("UNUSED_PARAMETER") value) {}

    fun getMethods(server: MinecraftServer): List<BoundMethod> {
        return ServerContext.get(server).peripheralMethods().getSelfMethods(this).map {
            BoundMethod(this, it.key, it.value)
        }
    }

    val operations: List<IPeripheralOperation<*>>
        get() = emptyList()

    val additionalType: String?
        get() = null
}
