package site.siredvin.tweakium.modules.peripheral.api

import dan200.computercraft.api.lua.LuaException

fun interface IPeripheralFunction<T, R> {
    @Throws(LuaException::class)
    fun apply(var1: T): R
}
