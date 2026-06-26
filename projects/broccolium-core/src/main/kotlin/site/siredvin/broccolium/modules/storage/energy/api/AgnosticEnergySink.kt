package site.siredvin.broccolium.modules.storage.energy.api

import site.siredvin.broccolium.modules.storage.base.api.AgnosticSink
import site.siredvin.broccolium.modules.storage.energy.AgnosticEnergyStack

interface AgnosticEnergySink : AgnosticSink<AgnosticEnergyStack, Long> {
    val canReceive: Boolean
    val receiveRateLimit: Long
        get() = Long.MAX_VALUE
}
