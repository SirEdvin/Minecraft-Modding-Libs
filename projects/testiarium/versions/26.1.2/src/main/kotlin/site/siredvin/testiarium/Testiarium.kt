// SPDX-FileCopyrightText: 2022 The CC: Tweaked Developers
// SPDX-FileCopyrightText: 2026 SirEdvin
// SPDX-License-Identifier: MPL-2.0
package site.siredvin.testiarium

import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.core.registries.Registries
import net.minecraft.gametest.framework.FunctionGameTestInstance
import net.minecraft.gametest.framework.GameTestHelper
import net.minecraft.gametest.framework.GameTestInstance
import net.minecraft.gametest.framework.GlobalTestReporter
import net.minecraft.gametest.framework.LogTestReporter
import net.minecraft.gametest.framework.StructureUtils
import net.minecraft.gametest.framework.TestData
import net.minecraft.gametest.framework.TestEnvironmentDefinition
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.server.MinecraftServer
import site.siredvin.testiarium.api.ClientGameTest
import site.siredvin.testiarium.api.GameTest
import site.siredvin.testiarium.api.TestGroup
import site.siredvin.testiarium.api.TestTags
import site.siredvin.testiarium.report.JunitTestReporter
import site.siredvin.testiarium.report.MultiTestReporter
import java.io.File
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.Locale
import java.util.function.Consumer

object Testiarium {
    const val MOD_ID = "testiarium"
    private val testClasses = linkedSetOf<Class<*>>()
    private var definitions: List<TestDefinition>? = null

    @JvmStatic
    fun register(vararg classes: Class<*>) {
        testClasses += classes
    }

    @JvmStatic
    fun init() {
        System.getProperty("testiarium.structures")?.let { StructureUtils.testStructuresSourceDir = File(it).toPath() }
        System.getProperty("testiarium.gametest-report")?.let {
            GlobalTestReporter.replaceWith(MultiTestReporter(JunitTestReporter(File(it)), LogTestReporter()))
        }
    }

    @JvmStatic
    fun onServerStarted(@Suppress("UNUSED_PARAMETER") server: MinecraftServer) = Unit

    @JvmStatic
    fun getTestOrigin(@Suppress("UNUSED_PARAMETER") server: MinecraftServer): BlockPos = BlockPos(0, -59, 0)

    @JvmStatic
    fun onServerStopped() = GlobalTestReporter.finish()

    /** Collect once before the static function registry freezes; instance registries reload per world. */
    @JvmStatic
    fun loadTests(fallbackRegister: Consumer<Method>, registerFunction: java.util.function.BiConsumer<Identifier, Consumer<GameTestHelper>>) {
        if (definitions != null) return
        val discovered = discoverTests(fallbackRegister)
        discovered.forEach { test -> registerFunction.accept(test.id, Consumer { invoke(test.method, it) }) }
        definitions = discovered
    }

    fun discoverTests(fallbackRegister: Consumer<Method>): List<TestDefinition> {
        val result = linkedMapOf<Identifier, TestDefinition>()
        testClasses.forEach { testClass ->
            testClass.declaredMethods.sortedBy { it.name }.forEach methods@{ method ->
                val group = method.getAnnotation(TestGroup::class.java)?.value
                    ?: testClass.getAnnotation(TestGroup::class.java)?.value
                    ?: if (method.isAnnotationPresent(ClientGameTest::class.java)) TestTags.CLIENT else TestTags.COMMON
                if (!TestTags.isEnabled(group)) return@methods
                val test = method.getAnnotation(GameTest::class.java)
                val client = method.getAnnotation(ClientGameTest::class.java)
                if (test == null && client == null) {
                    fallbackRegister.accept(method)
                    return@methods
                }
                require(method.parameterTypes.contentEquals(arrayOf(GameTestHelper::class.java))) { "GameTest method must accept one GameTestHelper: $method" }
                val name = "${testClass.simpleName}.${method.name}"
                val id = Identifier.fromNamespaceAndPath(MOD_ID, name.lowercase(Locale.ROOT))
                val template = test?.template ?: client!!.template
                val structure = Identifier.parse(template.ifEmpty { name.lowercase(Locale.ROOT) })
                val definition = TestDefinition(id, name, method, structure, test ?: GameTest(timeoutTicks = client!!.timeoutTicks))
                require(result.putIfAbsent(id, definition) == null) { "Duplicate GameTest identity $id ($method)" }
            }
        }
        val batches = result.values.map { it.metadata.batch }.distinct()
        val batchIds = batches.map { Identifier.fromNamespaceAndPath(MOD_ID, it.lowercase(Locale.ROOT)) }
        require(batchIds.distinct().size == batches.size) { "GameTest batch identifiers collide after normalization: $batches" }
        return result.values.toList()
    }

    @JvmStatic
    fun registerInstances(
        registerEnvironment: java.util.function.BiFunction<Identifier, TestEnvironmentDefinition<*>, Holder<TestEnvironmentDefinition<*>>>,
        registerTest: java.util.function.BiConsumer<Identifier, GameTestInstance>,
    ) {
        val tests = definitions ?: return
        val environments = tests.map { it.metadata.batch }.distinct().associateWith { batch ->
            registerEnvironment.apply(
                Identifier.fromNamespaceAndPath(MOD_ID, batch.lowercase(Locale.ROOT)),
                TestEnvironmentDefinition.AllOf(),
            )
        }
        tests.forEach { test ->
            val data = test.metadata
            registerTest.accept(
                test.id,
                FunctionGameTestInstance(
                    ResourceKey.create(Registries.TEST_FUNCTION, test.id),
                    TestData(
                        environments.getValue(data.batch), test.structure, data.timeoutTicks, data.setupTicks, data.required,
                        StructureUtils.getRotationForRotationSteps(data.rotationSteps), data.manualOnly, data.attempts,
                        data.requiredSuccesses, data.skyAccess, data.padding,
                    ),
                ),
            )
        }
    }

    @JvmStatic
    fun testName(id: Identifier): String = definitions?.firstOrNull { it.id == id }?.name ?: id.toString()

    data class TestDefinition(val id: Identifier, val name: String, val method: Method, val structure: Identifier, val metadata: GameTest)

    private fun invoke(method: Method, argument: Any) {
        try {
            val instance = if (Modifier.isStatic(method.modifiers)) null else method.declaringClass.getConstructor().newInstance()
            method.invoke(instance, argument)
        } catch (exception: InvocationTargetException) {
            throw (exception.cause as? RuntimeException ?: RuntimeException(exception.cause))
        } catch (exception: ReflectiveOperationException) {
            throw RuntimeException(exception)
        }
    }
}
