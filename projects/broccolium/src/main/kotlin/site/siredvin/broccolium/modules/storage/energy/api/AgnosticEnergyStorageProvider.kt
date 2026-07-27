package site.siredvin.broccolium.modules.storage.energy.api

interface AgnosticEnergyStorageProvider : AgnosticEnergySinkProvider {
    val energyStorage: AgnosticEnergyStorage
    override val energySink: AgnosticEnergySink
        get() = energyStorage
}
