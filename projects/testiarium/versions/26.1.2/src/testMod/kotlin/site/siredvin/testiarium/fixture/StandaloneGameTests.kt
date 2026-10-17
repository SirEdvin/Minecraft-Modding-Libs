package site.siredvin.testiarium.fixture

import net.minecraft.core.BlockPos
import net.minecraft.gametest.framework.GameTestAssertException
import net.minecraft.gametest.framework.GameTestHelper
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Blocks
import site.siredvin.testiarium.api.ClientGameTest
import site.siredvin.testiarium.api.GameTest
import site.siredvin.testiarium.api.TestGroup
import site.siredvin.testiarium.api.TestTags
import site.siredvin.testiarium.api.assertContainer
import site.siredvin.testiarium.api.getContainer
import site.siredvin.testiarium.api.sequence
import site.siredvin.testiarium.api.thenExecuteFailFast
import site.siredvin.testiarium.fixture.client.thenOnClient
import site.siredvin.testiarium.fixture.client.thenScreenshot
import site.siredvin.testiarium.report.JunitTestReporter
import site.siredvin.testiarium.report.MultiTestReporter
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

private fun GameTestAssertException(message: String) = GameTestAssertException(net.minecraft.network.chat.Component.literal(message), 0)

class StandaloneGameTests {
    @GameTest(template = "empty")
    fun nativeEnvironmentSetupAndTeardown(helper: GameTestHelper) {
        val environment = helper.level.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.TEST_ENVIRONMENT)
            .getOrThrow(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.TEST_ENVIRONMENT, net.minecraft.resources.Identifier.parse("testiarium:defaultbatch"))).value()
        check(environment is net.minecraft.gametest.framework.TestEnvironmentDefinition.SetGameRules) { "The native batch must own its rule setup/teardown" }
        val rules = helper.level.gameRules
        check(!rules.get(net.minecraft.world.level.gamerules.GameRules.ADVANCE_TIME))
        check(!rules.get(net.minecraft.world.level.gamerules.GameRules.SPAWN_MOBS))
        val rule = net.minecraft.world.level.gamerules.GameRules.ADVANCE_WEATHER
        val original = rules.get(rule)
        val changed = net.minecraft.world.level.gamerules.GameRuleMap.of().apply { set(rule, !original) }
        val activation = net.minecraft.gametest.framework.TestEnvironmentDefinition.activate(net.minecraft.gametest.framework.TestEnvironmentDefinition.SetGameRules(changed), helper.level)
        try {
            check(rules.get(rule) == !original)
        } finally {
            activation.teardown()
        }
        check(rules.get(rule) == original) { "Native environment teardown did not restore the rule" }
        val sourceProperty = System.getProperty("testiarium.fixture-source")
        val packProperty = System.getProperty("testiarium.structures")
        val probe = File(System.getProperty("testiarium.gametest-report")).parentFile.resolve("fixture-command-probe")
        val source = probe.resolve("source").also { it.mkdirs() }
        val pack = probe.resolve("pack")
        val fixture = source.resolve("empty.snbt")
        fixture.writeText(File(sourceProperty, "empty.snbt").readText())
        try {
            System.setProperty("testiarium.fixture-source", source.absolutePath)
            System.setProperty("testiarium.structures", pack.absolutePath)
            val dispatcher = helper.level.server.commands.dispatcher
            val commandSource = helper.level.server.createCommandSourceStack()
            check(dispatcher.execute("testiarium import", commandSource) == 1)
            val imported = pack.resolve("data/minecraft/structure/empty.snbt")
            check(imported.readText() == fixture.readText())
            imported.appendText("\n")
            check(dispatcher.execute("testiarium export", commandSource) == 1)
            check(fixture.readText() == imported.readText())
        } finally {
            System.setProperty("testiarium.fixture-source", sourceProperty)
            System.setProperty("testiarium.structures", packProperty)
        }
        helper.succeed()
    }

    @GameTest(template = "empty")
    fun passes(helper: GameTestHelper) {
        helper.succeed()
    }

    @GameTest(template = "empty", required = false)
    fun requiredFailure(helper: GameTestHelper): Unit = throw GameTestAssertException("required failure fixture")

    @GameTest(template = "empty", required = false)
    fun optionalFailure(helper: GameTestHelper): Unit = throw GameTestAssertException("optional failure fixture")

    @GameTest(template = "empty")
    fun failFastSequence(helper: GameTestHelper) = helper.sequence {
        thenExecuteFailFast { check(true) }
    }

    @GameTest(template = "empty", required = false)
    fun failFastFailureCannotBecomeSuccess(helper: GameTestHelper) = helper.sequence {
        thenExecuteFailFast { throw GameTestAssertException("fail-fast sentinel") }
        thenExecute { error("Must not execute after fail-fast sentinel") }
    }

    @GameTest(template = "empty", required = true)
    fun failFastAssertionIsImmediatelyTerminal(helper: GameTestHelper) = observeFailFast(helper, GameTestAssertException("immediate assertion sentinel"))

    @GameTest(template = "empty", required = true)
    fun failFastExceptionIsImmediatelyTerminal(helper: GameTestHelper) = observeFailFast(helper, IllegalStateException("immediate exception sentinel"))

    @GameTest(template = "empty", required = true)
    fun failFastErrorIsImmediatelyTerminal(helper: GameTestHelper) = observeFailFast(helper, AssertionError("immediate error sentinel"))

    @GameTest(template = "empty", required = true)
    fun containerMatchesIncludingImplicitEmptySlots(helper: GameTestHelper) {
        val pos = BlockPos.ZERO
        helper.setBlock(pos, Blocks.CHEST)
        val container = helper.getContainer(pos)
        helper.assertContainer(pos, emptyList())
        container.setItem(0, ItemStack(Items.DIAMOND, 2))
        helper.assertContainer(pos, listOf(ItemStack(Items.DIAMOND, 2)))
        helper.assertContainer(pos, List(container.containerSize + 2) { if (it == 0) ItemStack(Items.DIAMOND, 2) else ItemStack.EMPTY })
        helper.succeed()
    }

    @GameTest(template = "empty", required = true)
    fun containerRejectsMismatchedActualSlots(helper: GameTestHelper) {
        val pos = BlockPos.ZERO
        helper.setBlock(pos, Blocks.CHEST)
        val container = helper.getContainer(pos)
        container.setItem(0, ItemStack(Items.DIAMOND, 2))
        expectContainerMismatch(helper, pos, listOf(ItemStack(Items.DIAMOND)), 0)
        expectContainerMismatch(helper, pos, listOf(ItemStack(Items.EMERALD, 2)), 0)
        container.setItem(1, ItemStack(Items.EMERALD))
        expectContainerMismatch(helper, pos, listOf(ItemStack(Items.DIAMOND, 2)), 1)
        helper.succeed()
    }

    @GameTest(template = "empty", required = true)
    fun containerRejectsNonemptyExpectedSlotsBeyondItsSize(helper: GameTestHelper) {
        val pos = BlockPos.ZERO
        helper.setBlock(pos, Blocks.CHEST)
        val size = helper.getContainer(pos).containerSize
        for (slot in listOf(size, size + 2)) {
            expectContainerMismatch(helper, pos, List(slot + 1) { if (it == slot) ItemStack(Items.DIAMOND) else ItemStack.EMPTY }, slot)
        }
        helper.succeed()
    }

    @GameTest(template = "empty")
    fun reporterCountsEscapesAndFinishesOnce(helper: GameTestHelper) {
        val destination = File(System.getProperty("testiarium.gametest-report")).parentFile.resolve("reporter-contract.xml")
        val reporter = JunitTestReporter(destination)
        val required = ReporterFixtures.failed(helper.level, true, "<script> & \"required\"")
        val optional = ReporterFixtures.failed(helper.level, false, "optional fixture")
        val multi = MultiTestReporter(reporter)
        multi.onTestSuccess(optional)
        multi.onTestFailed(required)
        multi.onTestFailed(optional)
        multi.finish()
        val first = destination.readText()
        multi.finish()
        check(first == destination.readText())
        val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(destination)
        check(document.documentElement.getAttribute("tests") == "3")
        check(document.documentElement.getAttribute("failures") == "1")
        check(document.documentElement.getAttribute("skipped") == "1")
        check(document.getElementsByTagName("testcase").length == 3)
        check(document.getElementsByTagName("failure").item(0).attributes.getNamedItem("message").nodeValue == "<script> & \"required\"")
        val html = destination.resolveSibling("reporter-contract.html").readText()
        check("<script>" !in html)
        check("&lt;script&gt; &amp; &quot;required&quot;" in html)
        helper.succeed()
    }

    @ClientGameTest(template = "empty")
    @TestGroup(TestTags.CLIENT)
    fun clientFixture(helper: GameTestHelper) = helper.sequence {
        thenOnClient { check(minecraft.player != null) }
        thenScreenshot("fixture")
    }
}

private fun observeFailFast(helper: GameTestHelper, sentinel: Throwable) {
    // This child has no global ticker/report listener: only the required outer test can pass.
    val child = ReporterFixtures.running(helper.level)
    var actions = 0
    var laterActions = 0
    val sequence = GameTestHelper(child).startSequence().thenExecuteFailFast {
        actions++
        throw sentinel
    }.thenExecute { laterActions++ }
    sequence.thenSucceed()
    sequence.tickAndContinue(0)
    check(child.isDone && child.hasFailed() && !child.hasSucceeded()) { "Fail-fast must fail the child immediately, before timeout" }
    val failure = child.error
    check(failure?.message == sentinel.message) { "Fail-fast lost the original failure: $failure" }
    if (sentinel is GameTestAssertException) check(failure === sentinel)
    check(actions == 1 && laterActions == 0) { "Fail-fast must execute once and stop later callbacks" }

    // Exercise the native completed-test guard, then a separate delayed success sequence.
    // Pumping a failed sequence directly again would bypass GameTestInfo's lifecycle guard.
    helper.runAfterDelay(2) {
        ReporterFixtures.tick(child)
        var competingActions = 0
        val competing = GameTestHelper(child).startSequence().thenExecute { competingActions++ }
        competing.thenSucceed()
        competing.tickAndContinue(2)
        check(competingActions == 1) { "The competing success attempt was not exercised" }
        check(child.isDone && child.hasFailed() && !child.hasSucceeded()) { "Delayed success hid the fail-fast failure" }
        check(child.error === failure) { "Delayed success changed the original failure" }
        check(actions == 1 && laterActions == 0) { "Fail-fast action retried or a later callback ran" }
        helper.succeed()
    }
}

private fun expectContainerMismatch(helper: GameTestHelper, pos: BlockPos, expected: List<ItemStack>, slot: Int) {
    var failure: GameTestAssertException? = null
    try {
        helper.assertContainer(pos, expected)
    } catch (error: GameTestAssertException) {
        failure = error
    }
    check(failure?.message?.startsWith("Container differs at slot $slot.") == true) { "Expected a container mismatch at slot $slot, got $failure" }
}
