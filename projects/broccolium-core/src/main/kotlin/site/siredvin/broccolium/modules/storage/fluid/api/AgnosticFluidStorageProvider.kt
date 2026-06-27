package site.siredvin.broccolium.modules.storage.fluid.api

import site.siredvin.broccolium.modules.storage.base.api.AgnosticSink
import site.siredvin.broccolium.modules.storage.fluid.AgnosticFluidStack

interface AgnosticFluidStorageProvider : AgnosticFluidSinkProvider {
    val fluidStorage: AgnosticFluidStorage
    override val fluidSink: AgnosticSink<AgnosticFluidStack, Double>
        get() = fluidStorage
}
