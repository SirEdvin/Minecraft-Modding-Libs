package site.siredvin.tweakium.modules.platform

import site.siredvin.broccolium.modules.platform.api.InnerPlatformToolkit
import site.siredvin.tweakium.modules.platform.api.InnerComputerPlatformToolkit

object ComputerPlatformToolkit {
    private var impl: InnerComputerPlatformToolkit? = null

    fun configure(impl: InnerComputerPlatformToolkit) {
        this.impl = impl
    }

    fun get(): InnerComputerPlatformToolkit {
        if (impl == null) {
            throw IllegalStateException("You should init Peripheral Platform first")
        }
        return impl!!
    }
}