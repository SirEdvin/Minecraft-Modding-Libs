package site.siredvin.testiarium

import net.minecraft.gametest.framework.GameTest
import net.minecraft.gametest.framework.GameTestHelper
import net.minecraft.gametest.framework.TestFunction
import site.siredvin.testiarium.api.TestGroup
import kotlin.test.assertFalse

internal fun assertVersionMetadata(function: TestFunction) {
    assertFalse(function.isRequired)
}

@TestGroup("disabled")
class RegistrationFixture {
    companion object {
        val failure = IllegalStateException("original cause")
    }

    @TestGroup("common")
    @GameTest(
        template = "explicit_template",
        timeoutTicks = 77,
        required = false,
        batch = "metadata_batch",
        rotationSteps = 1,
        setupTicks = 12,
        attempts = 5,
        requiredSuccesses = 2,
    )
    fun enabled(helper: GameTestHelper) = helper.succeed()

    @TestGroup("common")
    @GameTest
    fun throwsOriginal(@Suppress("UNUSED_PARAMETER") helper: GameTestHelper): Unit = throw failure

    @GameTest
    fun disabled(helper: GameTestHelper) = helper.succeed()

    @TestGroup("common")
    fun fallback() { }
}
