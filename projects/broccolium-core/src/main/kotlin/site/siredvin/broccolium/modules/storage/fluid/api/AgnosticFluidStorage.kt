package site.siredvin.broccolium.modules.storage.fluid.api

import site.siredvin.broccolium.modules.storage.base.api.AgnosticStorage
import site.siredvin.broccolium.modules.storage.fluid.AgnosticFluidStack

interface AgnosticFluidStorage : AgnosticStorage<AgnosticFluidStack, Double> {
    fun getCapacities(): List<Double> = emptyList()
}
