package site.siredvin.broccolium.test

import org.junit.jupiter.api.Test

@WithMinecraft
class FabricTargetAccessTests {
    @Test
    fun allSevenNativeAccessRulesAndOpenInventoryAreTransformed() {
        TargetAccessAssertions.verify()
    }
}
