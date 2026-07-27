package site.siredvin.tweakium.modules.peripheral.api

import site.siredvin.broccolium.modules.base.api.IConfigHandler

interface IPeripheralOperation<T> : IConfigHandler {
    fun getCooldown(context: T): Int
    fun getCost(context: T): Int
    fun computerDescription(): Map<String, Any?>
}
