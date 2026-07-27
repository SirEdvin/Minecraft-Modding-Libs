package site.siredvin.broccolium.modules.platform

import site.siredvin.broccolium.modules.platform.api.InnerPlatformTags

object PlatformTags {
    private var impl: InnerPlatformTags? = null

    fun configure(impl: InnerPlatformTags) {
        this.impl = impl
    }

    fun get(): InnerPlatformTags {
        if (impl == null) {
            throw IllegalStateException("You should init Peripheral Platform first")
        }
        return impl!!
    }
}
