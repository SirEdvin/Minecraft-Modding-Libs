package site.siredvin.testiarium

import net.minecraft.gametest.framework.GameTest
import net.minecraft.gametest.framework.GameTestHelper
import net.minecraft.gametest.framework.GameTestInfo
import net.minecraft.gametest.framework.GameTestRegistry
import net.minecraft.world.level.block.Rotation
import org.junit.jupiter.api.Test
import site.siredvin.testiarium.api.ClientGameTest
import site.siredvin.testiarium.api.TestGroup
import java.lang.reflect.Method
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame
import kotlin.test.assertTrue

class RegistrationTests {
    @Test
    fun groupingDeduplicationFallbackMetadataAndInvocationArePreserved() = withIsolatedRegistration {
        val functions = GameTestRegistry.getAllTestFunctions()
        val names = GameTestRegistry.getAllTestClassNames()
        Testiarium.register(RegistrationFixture::class.java, RegistrationFixture::class.java, DefaultFixture::class.java, StaticFixture::class.java)
        val fallback = mutableListOf<Method>()
        Testiarium.loadTests { fallback += it }
        val registered = functions.associateBy { it.testName }
        assertEquals(
            setOf("RegistrationFixture.enabled", "RegistrationFixture.throwsOriginal", "DefaultFixture.defaultTemplate", "StaticFixture.throwsOriginal"),
            registered.keys,
        )
        assertEquals(4, functions.size)
        val enabled = registered.getValue("RegistrationFixture.enabled")
        assertEquals("explicit_template", enabled.structureName)
        assertEquals(77, enabled.maxTicks)
        assertEquals("metadata_batch", enabled.batchName)
        assertEquals(Rotation.CLOCKWISE_90, enabled.rotation)
        assertEquals(12L, enabled.setupTicks)
        assertEquals(5, enabled.maxAttempts)
        assertEquals(2, enabled.requiredSuccesses)
        assertVersionMetadata(enabled)
        assertEquals("DefaultFixture.defaultTemplate", registered.getValue("DefaultFixture.defaultTemplate").structureName)
        assertEquals(listOf("fallback"), fallback.map { it.name })
        assertTrue("RegistrationFixture" in names)
        assertTrue("DefaultFixture" in names)
        assertTrue("StaticFixture" in names)
        // These invocation fixtures never touch a world/helper field: they only throw sentinels.
        val helper = GameTestHelper::class.java.getConstructor(GameTestInfo::class.java).newInstance(null)
        val error = assertFailsWith<IllegalStateException> { registered.getValue("RegistrationFixture.throwsOriginal").run(helper) }
        assertSame(RegistrationFixture.failure, error)
        val staticError = assertFailsWith<IllegalStateException> { registered.getValue("StaticFixture.throwsOriginal").run(helper) }
        assertSame(StaticFixture.failure, staticError)
    }

    @Test
    fun registrationStateIsRestoredAfterSuccess() = checkRegistrationRestoration(false)

    @Test
    fun registrationStateIsRestoredAfterFailure() = checkRegistrationRestoration(true)

    private fun checkRegistrationRestoration(throwFailure: Boolean) {
        val classes = registeredClasses()
        val savedClasses = classes.toList()
        val functions = GameTestRegistry.getAllTestFunctions()
        val savedFunctions = functions.toList()
        val names = GameTestRegistry.getAllTestClassNames()
        val savedNames = names.toList()
        try {
            classes.clear()
            Testiarium.register(DefaultFixture::class.java)
            val sentinel = IllegalStateException("registration isolation sentinel")
            val runFixture = {
                withIsolatedRegistration {
                    Testiarium.register(RegistrationFixture::class.java)
                    Testiarium.loadTests { }
                    if (throwFailure) throw sentinel
                }
            }
            if (throwFailure) {
                assertSame(sentinel, assertFailsWith<IllegalStateException> { runFixture() })
            } else {
                runFixture()
            }
            assertEquals(listOf(DefaultFixture::class.java), classes.toList(), "Testiarium test classes must be restored")
            assertEquals(savedFunctions, functions.toList())
            assertEquals(savedNames, names.toList())
        } finally {
            classes.clear()
            classes.addAll(savedClasses)
            functions.clear()
            functions.addAll(savedFunctions)
            names.clear()
            names.addAll(savedNames)
        }
    }

    private fun withIsolatedRegistration(action: () -> Unit) {
        val classes = registeredClasses()
        val savedClasses = classes.toList()
        val functions = GameTestRegistry.getAllTestFunctions()
        val names = GameTestRegistry.getAllTestClassNames()
        val savedFunctions = functions.toList()
        val savedNames = names.toList()
        try {
            classes.clear()
            functions.clear()
            names.clear()
            action()
        } finally {
            classes.clear()
            classes.addAll(savedClasses)
            functions.clear()
            functions.addAll(savedFunctions)
            names.clear()
            names.addAll(savedNames)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun registeredClasses(): MutableSet<Class<*>> = Testiarium::class.java.getDeclaredField("testClasses").also { it.isAccessible = true }.get(null) as MutableSet<Class<*>>

    class DefaultFixture {
        @GameTest
        fun defaultTemplate(helper: GameTestHelper) = helper.succeed()

        @ClientGameTest
        fun clientExcluded(helper: GameTestHelper) = helper.succeed()
    }

    @TestGroup("disabled")
    class StaticFixture private constructor() {
        companion object {
            val failure = IllegalStateException("static original cause")

            @JvmStatic
            @TestGroup("common")
            @GameTest
            fun throwsOriginal(@Suppress("UNUSED_PARAMETER") helper: GameTestHelper): Unit = throw failure
        }
    }
}
