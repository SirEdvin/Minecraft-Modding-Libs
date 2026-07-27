package site.siredvin.broccolium.modules.platform

import site.siredvin.broccolium.modules.platform.api.InnerPlatformToolkit

object PlatformToolkit {
    private var impl: InnerPlatformToolkit? = null

    fun configure(impl: InnerPlatformToolkit) {
        this.impl = impl
    }

    fun get(): InnerPlatformToolkit {
        if (impl == null) {
            throw IllegalStateException("You should init Peripheral Platform first")
        }
        return impl!!
    }
}
