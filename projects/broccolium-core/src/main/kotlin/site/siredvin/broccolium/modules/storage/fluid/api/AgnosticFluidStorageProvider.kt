package site.siredvin.broccolium.modules.storage.fluid.api

interface AgnosticFluidStorageProvider : AgnosticFluidSinkProvider {
    val fluidStorage: AgnosticFluidStorage
    override val fluidSink: AgnosticFluidSink
        get() = fluidStorage
}
