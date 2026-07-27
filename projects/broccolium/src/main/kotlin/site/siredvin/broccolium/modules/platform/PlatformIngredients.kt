package site.siredvin.broccolium.modules.platform

import site.siredvin.broccolium.modules.platform.api.InnerPlatformIngredients

object PlatformIngredients {
    private var impl: InnerPlatformIngredients? = null

    fun configure(impl: InnerPlatformIngredients) {
        this.impl = impl
    }

    fun get(): InnerPlatformIngredients {
        if (impl == null) {
            throw IllegalStateException("You should init recipe ingredients first")
        }
        return impl!!
    }
}
