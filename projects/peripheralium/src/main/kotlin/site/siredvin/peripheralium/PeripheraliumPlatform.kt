package site.siredvin.peripheralium

import site.siredvin.broccolium.modules.platform.BasePlatform
import site.siredvin.broccolium.modules.platform.ModInformationTracker
import site.siredvin.broccolium.modules.platform.api.InnerBasePlatform

object PeripheraliumPlatform : BasePlatform() {

    override val baseInnerPlatform: InnerBasePlatform
        get() = impl!!
    override val modInformationTracker: ModInformationTracker = ModInformationTracker()

    private var impl: InnerBasePlatform? = null

    fun configure(impl: InnerBasePlatform) {
        this.impl = impl
    }
}
