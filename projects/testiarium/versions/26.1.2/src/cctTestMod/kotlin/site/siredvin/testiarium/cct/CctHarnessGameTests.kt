package site.siredvin.testiarium.cct

import dan200.computercraft.shared.computer.blocks.AbstractComputerBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.gametest.framework.GameTestHelper
import net.minecraft.resources.Identifier
import net.minecraft.world.level.block.Blocks
import site.siredvin.testiarium.api.GameTest
import site.siredvin.testiarium.api.thenExecuteFailFast
import site.siredvin.testiarium.fixture.ReporterFixtures

class CctHarnessGameTests {
    @GameTest(template = "empty", timeoutTicks = 400)
    fun realLuaAndQueuedActionExecuteOnce(helper: GameTestHelper) {
        val label = "harness.pass"
        var actions = 0
        val monitor = CctComputers.enqueue(helper.level.server, label) {
            check(id == 1)
            actions++
        }
        startComputer(helper, label)
        helper.thenLua(label).thenExecuteFailFast {
            monitor.check()
            check(actions == 1)
            check(CctComputerState.get(label)?.isDone("ready") == true)
        }.thenSucceed()
    }

    @GameTest(template = "empty", timeoutTicks = 400, required = false)
    fun luaFailureCannotBecomeSuccess(helper: GameTestHelper) {
        startComputer(helper, "harness.fail")
        helper.thenLua("harness.fail").thenExecute { error("Lua failure continued") }.thenSucceed()
    }

    @GameTest(template = "empty", timeoutTicks = 400)
    fun luaFailureIsObservedByRequiredOuterTest(helper: GameTestHelper) {
        val label = "harness.observe"
        startComputer(helper, label)
        helper.startSequence().thenWaitUntil {
            if (CctComputerState.get(label)?.isDone(CctComputerState.DONE) != true) throw GameTestAssertException("Waiting for real Lua error")
        }.thenExecuteFailFast {
            val state = checkNotNull(CctComputerState.get(label))
            val child = ReporterFixtures.running(helper.level)
            var actions = 0
            var later = 0
            val childSequence = GameTestHelper(child).startSequence().thenExecuteFailFast {
                actions++
                state.check(CctComputerState.DONE)
            }.thenExecute { later++ }
            childSequence.thenSucceed()
            childSequence.tickAndContinue(0)
            check(child.isDone && child.hasFailed() && !child.hasSucceeded())
            val failure = child.error
            check(failure?.description?.string?.startsWith("cct lua sentinel") == true)
            check(actions == 1 && later == 0)
            helper.runAfterDelay(2) {
                ReporterFixtures.tick(child)
                GameTestHelper(child).startSequence().also { it.thenSucceed(); it.tickAndContinue(2) }
                check(child.error === failure && child.hasFailed() && !child.hasSucceeded())
                check(actions == 1 && later == 0)
                helper.succeed()
            }
        }
        // The delayed outer assertion, not this sequence, owns success.

    }

    private fun startComputer(helper: GameTestHelper, label: String) {
        val pos = BlockPos(1, 1, 1)
        helper.setBlock(pos, BuiltInRegistries.BLOCK.getValue(Identifier.parse("computercraft:computer_normal")))
        val computer = helper.getBlockEntity(pos, AbstractComputerBlockEntity::class.java)
        computer.setComputerID(1)
        computer.setLabel(label)
        helper.runBeforeTestEnd { helper.setBlock(pos, Blocks.AIR) }
        computer.createServerComputer().turnOn()
    }
}
