package site.siredvin.testiarium

import net.minecraft.SharedConstants
import net.minecraft.core.Holder
import net.minecraft.gametest.framework.GameTestHelper
import net.minecraft.gametest.framework.GameTestInstance
import net.minecraft.gametest.framework.TestEnvironmentDefinition
import net.minecraft.resources.Identifier
import net.minecraft.server.Bootstrap
import net.minecraft.world.level.block.Rotation
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import site.siredvin.testiarium.api.ClientGameTest
import site.siredvin.testiarium.api.GameTest
import site.siredvin.testiarium.api.TestGroup
import java.lang.reflect.Method
import java.util.function.Consumer
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame
import kotlin.test.assertTrue

class RegistrationTests {
    companion object {
        @JvmStatic @BeforeAll
        fun bootstrap() {
            SharedConstants.tryDetectVersion()
            Bootstrap.bootStrap()
        }
    }

    @Test
    fun groupsDeduplicationMetadataAndNativeInstances() = isolated {
        Testiarium.register(Fixture::class.java, Fixture::class.java)
        val fallback = mutableListOf<Method>()
        val functions = linkedMapOf<Identifier, Consumer<GameTestHelper>>()
        Testiarium.loadTests({ fallback += it }, { id, function -> check(functions.put(id, function) == null) })
        Testiarium.loadTests({ error("Repeated discovery") }, { _, _ -> error("Repeated function registration") })
        assertEquals(listOf("fallback"), fallback.map { it.name })
        assertEquals(setOf("enabled", "throwsoriginal"), functions.keys.map { it.path.substringAfter('.') }.toSet())
        val instances = linkedMapOf<Identifier, GameTestInstance>()
        val environments = linkedMapOf<Identifier, TestEnvironmentDefinition<*>>()
        Testiarium.registerInstances({ id, environment ->
            environments[id] = environment
            Holder.direct(environment)
        }, { id, test -> instances[id] = test })
        assertEquals(2, instances.size)
        val test = instances.getValue(Identifier.parse("testiarium:fixture.enabled"))
        assertEquals(Identifier.parse("example:explicit_template"), test.structure())
        assertEquals(77, test.maxTicks())
        assertEquals(12, test.setupTicks())
        assertEquals(5, test.maxAttempts())
        assertEquals(2, test.requiredSuccesses())
        assertEquals(Rotation.CLOCKWISE_90, test.rotation())
        assertEquals(3, test.padding())
        assertTrue(test.manualOnly() && test.skyAccess() && !test.required())
        assertEquals(setOf("metadata_batch", "defaultbatch"), environments.keys.map { it.path }.toSet())
        val helper = GameTestHelper::class.java.constructors.single().newInstance(null) as GameTestHelper
        val function = functions.getValue(Identifier.parse("testiarium:fixture.throwsoriginal"))
        assertSame(Fixture.failure, assertFailsWith<IllegalStateException> { function.accept(helper) })
        // World reloads get fresh native instances, not a process-global mutable instance registry.
        var reloadCount = 0
        Testiarium.registerInstances({ _, environment -> Holder.direct(environment) }, { _, _ -> reloadCount++ })
        assertEquals(2, reloadCount)
    }

    @Test
    fun defaultNamesAndStaticInvocation() = isolated {
        Testiarium.register(DefaultFixture::class.java, StaticFixture::class.java)
        val functions = linkedMapOf<Identifier, Consumer<GameTestHelper>>()
        Testiarium.loadTests({ }, { id, function -> functions[id] = function })
        val definition = Testiarium.discoverTests { }.first { it.name == "DefaultFixture.defaults" }
        assertEquals(Identifier.parse("minecraft:defaultfixture.defaults"), definition.structure)
        val helper = GameTestHelper::class.java.constructors.single().newInstance(null) as GameTestHelper
        assertSame(StaticFixture.failure, assertFailsWith<IllegalStateException> { functions.getValue(Identifier.parse("testiarium:staticfixture.throwsoriginal")).accept(helper) })
    }

    @Test
    fun constructorAndReflectionErrorsRemainExplicit() = isolated {
        Testiarium.register(NoDefaultConstructor::class.java)
        var function: Consumer<GameTestHelper>? = null
        Testiarium.loadTests({ }, { _, body -> function = body })
        val helper = GameTestHelper::class.java.constructors.single().newInstance(null) as GameTestHelper
        assertTrue(assertFailsWith<RuntimeException> { function!!.accept(helper) }.cause is NoSuchMethodException)
    }

    @Test
    fun duplicateIdentifiersAreRejectedBeforeStaticRegistryMutation() = isolated {
        Testiarium.register(One.SameName::class.java, Two.SameName::class.java)
        var writes = 0
        assertFailsWith<IllegalArgumentException> { Testiarium.loadTests({ }, { _, _ -> writes++ }) }
        assertEquals(0, writes)
    }

    @Test
    fun batchNormalizationCollisionsAreRejectedBeforeStaticRegistryMutation() = isolated {
        Testiarium.register(BatchCollision::class.java)
        var writes = 0
        assertFailsWith<IllegalArgumentException> { Testiarium.loadTests({ }, { _, _ -> writes++ }) }
        assertEquals(0, writes)
    }

    @Suppress("UNCHECKED_CAST")
    private fun isolated(action: () -> Unit) {
        val field = Testiarium::class.java.getDeclaredField("testClasses").also { it.isAccessible = true }
        val classes = field.get(null) as MutableSet<Class<*>>
        val saved = classes.toList()
        val definitions = Testiarium::class.java.getDeclaredField("definitions").also { it.isAccessible = true }
        val savedDefinitions = definitions.get(null)
        try {
            classes.clear()
            definitions.set(null, null)
            action()
        } finally {
            classes.clear()
            classes.addAll(saved)
            definitions.set(null, savedDefinitions)
        }
    }

    @TestGroup("disabled")
    class Fixture {
        companion object {
            val failure = IllegalStateException("original cause")
        }

        @TestGroup("common")
        @GameTest(template = "example:explicit_template", batch = "metadata_batch", timeoutTicks = 77, setupTicks = 12, required = false, rotationSteps = 1, manualOnly = true, attempts = 5, requiredSuccesses = 2, skyAccess = true, padding = 3)
        fun enabled(helper: GameTestHelper) = helper.succeed()

        @TestGroup("common")
        @GameTest
        fun throwsOriginal(@Suppress("UNUSED_PARAMETER") helper: GameTestHelper): Unit = throw failure

        @TestGroup("common")
        fun fallback() = Unit

        @GameTest fun disabled(helper: GameTestHelper) = helper.succeed()
    }
    class DefaultFixture {
        @GameTest fun defaults(helper: GameTestHelper) = helper.succeed()

        @ClientGameTest fun clientExcluded(helper: GameTestHelper) = helper.succeed()
    }
    class StaticFixture private constructor() {
        companion object {
            val failure = IllegalStateException("static original cause")

            @JvmStatic @GameTest
            fun throwsOriginal(@Suppress("UNUSED_PARAMETER") helper: GameTestHelper): Unit = throw failure
        }
    }
    class NoDefaultConstructor(@Suppress("UNUSED_PARAMETER") value: Int) {
        @GameTest fun test(helper: GameTestHelper) = helper.succeed()
    }
    class One {
        class SameName {
            @GameTest fun test(helper: GameTestHelper) = helper.succeed()
        }
    }
    class Two {
        class SameName {
            @GameTest fun test(helper: GameTestHelper) = helper.succeed()
        }
    }
    class BatchCollision {
        @GameTest(batch = "Batch")
        fun first(helper: GameTestHelper) = helper.succeed()

        @GameTest(batch = "batch")
        fun second(helper: GameTestHelper) = helper.succeed()
    }
}
