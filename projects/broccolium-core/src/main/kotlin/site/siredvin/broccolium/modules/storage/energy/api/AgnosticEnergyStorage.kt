package site.siredvin.broccolium.modules.storage.energy.api

import site.siredvin.broccolium.modules.storage.base.api.AgnosticStorage
import site.siredvin.broccolium.modules.storage.energy.AgnosticEnergyStack

interface AgnosticEnergyStorage :
    AgnosticStorage<AgnosticEnergyStack, Long>,
    AgnosticEnergySink {
    val canExtract: Boolean
    val extractRateLimit: Long
        get() = Long.MAX_VALUE
    val firstEnergy: AgnosticEnergyStack
        get() = getContent().next()
}
