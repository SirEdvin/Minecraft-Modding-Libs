package site.siredvin.broccolium.modules.storage.fluid.api

import site.siredvin.broccolium.modules.storage.base.api.AgnosticSink
import site.siredvin.broccolium.modules.storage.fluid.AgnosticFluidStack

interface AgnosticFluidSinkProvider {
    val fluidSink: AgnosticSink<AgnosticFluidStack, Double>
}
