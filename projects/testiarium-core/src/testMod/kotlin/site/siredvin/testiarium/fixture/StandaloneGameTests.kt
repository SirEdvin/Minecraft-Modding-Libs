package site.siredvin.testiarium.fixture

import net.minecraft.gametest.framework.GameTest
import net.minecraft.gametest.framework.GameTestAssertException
import net.minecraft.gametest.framework.GameTestHelper

class StandaloneGameTests {
    @GameTest(template = "empty")
    fun passes(helper: GameTestHelper) {
        helper.succeed()
    }

    @GameTest(template = "empty")
    fun requiredFailure(helper: GameTestHelper) {
        throw GameTestAssertException("required failure fixture")
    }

    @GameTest(template = "empty", required = false)
    fun optionalFailure(helper: GameTestHelper) {
        throw GameTestAssertException("optional failure fixture")
    }
}
