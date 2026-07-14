package site.siredvin.testiarium.fixture

import net.minecraft.gametest.framework.GameTest
import net.minecraft.gametest.framework.GameTestAssertException
import net.minecraft.gametest.framework.GameTestHelper
import site.siredvin.testiarium.api.ClientGameTest
import site.siredvin.testiarium.api.TestGroup
import site.siredvin.testiarium.api.TestTags
import site.siredvin.testiarium.api.sequence
import site.siredvin.testiarium.api.thenExecuteFailFast
import site.siredvin.testiarium.fixture.client.thenOnClient
import site.siredvin.testiarium.fixture.client.thenScreenshot

class StandaloneGameTests {
    @GameTest(template = "empty")
    fun passes(helper: GameTestHelper) {
        helper.succeed()
    }

    @GameTest(template = "empty", required = false)
    fun requiredFailure(helper: GameTestHelper) {
        throw GameTestAssertException("required failure fixture")
    }

    @GameTest(template = "empty", required = false)
    fun optionalFailure(helper: GameTestHelper) {
        throw GameTestAssertException("optional failure fixture")
    }

    @GameTest(template = "empty")
    fun failFastSequence(helper: GameTestHelper) = helper.sequence {
        thenExecuteFailFast { check(true) }
    }

    @ClientGameTest(template = "empty")
    @TestGroup(TestTags.CLIENT)
    fun clientFixture(helper: GameTestHelper) = helper.sequence {
        thenOnClient { check(minecraft.player != null) }
        thenScreenshot("fixture")
    }
}
