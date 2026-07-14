// SPDX-FileCopyrightText: 2022 The CC: Tweaked Developers
// SPDX-FileCopyrightText: 2026 SirEdvin
// SPDX-License-Identifier: MPL-2.0
// Adapted from CC:Tweaked projects/common/src/testMod/kotlin/dan200/computercraft/gametest/core/TestHooks.kt

package site.siredvin.testiarium

import net.minecraft.gametest.framework.GameTest
import net.minecraft.gametest.framework.GameTestRunner
import net.minecraft.gametest.framework.GameTestTicker
import net.minecraft.gametest.framework.GameTestRegistry
import net.minecraft.gametest.framework.TestFunction
import net.minecraft.core.BlockPos
import net.minecraft.server.MinecraftServer
import net.minecraft.world.level.GameRules
import site.siredvin.testiarium.api.ClientGameTest
import site.siredvin.testiarium.api.TestGroup
import site.siredvin.testiarium.api.TestTags
import site.siredvin.testiarium.report.JunitTestReporter
import site.siredvin.testiarium.report.MultiTestReporter
import java.io.File
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.function.Consumer

object Testiarium {
    const val MOD_ID = "testiarium"

    private val testClasses = linkedSetOf<Class<*>>()

    @JvmStatic
    fun register(vararg classes: Class<*>) {
        testClasses += classes
    }

    @JvmStatic
    fun init() {
        System.getProperty("testiarium.structures")?.let {
            net.minecraft.gametest.framework.StructureUtils.testStructuresDir = it
        }
        System.getProperty("testiarium.gametest-report")?.let { output ->
            net.minecraft.gametest.framework.GlobalTestReporter.replaceWith(
                MultiTestReporter(
                    JunitTestReporter(File(output)),
                    net.minecraft.gametest.framework.LogTestReporter(),
                ),
            )
        }
    }

    @JvmStatic
    fun onServerStarted(server: MinecraftServer) {
        server.gameRules.getRule(GameRules.RULE_DAYLIGHT).set(false, server)
        server.overworld().dayTime = 6000
        GameTestRunner.clearAllTests(server.overworld(), BlockPos(0, -60, 0), GameTestTicker.SINGLETON, 200)
    }

    @JvmStatic
    fun onServerStopped() {
        net.minecraft.gametest.framework.GlobalTestReporter.finish()
    }

    @JvmStatic
    fun loadTests(fallbackRegister: Consumer<Method>) {
        testClasses.forEach { testClass ->
            testClass.declaredMethods.forEach { method -> registerTest(testClass, method, fallbackRegister) }
        }
    }

    private fun registerTest(testClass: Class<*>, method: Method, fallbackRegister: Consumer<Method>) {
        val group = method.getAnnotation(TestGroup::class.java)?.value
            ?: testClass.getAnnotation(TestGroup::class.java)?.value
            ?: if (method.isAnnotationPresent(ClientGameTest::class.java)) TestTags.CLIENT else TestTags.COMMON
        if (!TestTags.isEnabled(group)) return

        val testName = "${testClass.simpleName}.${method.name}"
        method.getAnnotation(GameTest::class.java)?.let { test ->
            GameTestRegistry.getAllTestFunctions().add(
                TestFunction(
                    test.batch,
                    testName,
                    test.template.ifEmpty { testName },
                    net.minecraft.gametest.framework.StructureUtils.getRotationForRotationSteps(test.rotationSteps),
                    test.timeoutTicks,
                    test.setupTicks,
                    test.required,
                    test.requiredSuccesses,
                    test.attempts,
                ) { helper -> invoke(method, helper) },
            )
            GameTestRegistry.getAllTestClassNames().add(testClass.simpleName)
            return
        }
        method.getAnnotation(ClientGameTest::class.java)?.let { test ->
            GameTestRegistry.getAllTestFunctions().add(
                TestFunction(testName, testName, test.template.ifEmpty { testName }, test.timeoutTicks, 0, true) { helper ->
                    invoke(method, helper)
                },
            )
            GameTestRegistry.getAllTestClassNames().add(testClass.simpleName)
            return
        }
        fallbackRegister.accept(method)
    }

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
