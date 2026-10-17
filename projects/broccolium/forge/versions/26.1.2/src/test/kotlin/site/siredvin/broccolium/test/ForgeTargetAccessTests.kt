package site.siredvin.broccolium.test

import org.junit.jupiter.api.Test

@WithNeoForgeRegistries
class ForgeTargetAccessTests {
    @Test
    fun allSevenNativeAccessRulesAndOpenInventoryAreTransformed() {
        TargetAccessAssertions.verify()
    }
}
