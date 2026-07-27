package site.siredvin.tweakium.modules.operation

import java.io.Serializable

class SphereOperationContext(val radius: Int) : Serializable {

    companion object {
        fun of(radius: Int): SphereOperationContext = SphereOperationContext(radius)
    }
}
