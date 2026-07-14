// SPDX-FileCopyrightText: 2022 The CC: Tweaked Developers
// SPDX-FileCopyrightText: 2026 SirEdvin
// SPDX-License-Identifier: MPL-2.0
// Adapted from CC:Tweaked 1.113.0 projects/common/src/testMod/kotlin/dan200/computercraft/gametest/api/TestExtensions.kt.

package site.siredvin.testiarium.cct

import net.minecraft.gametest.framework.GameTestAssertException
import net.minecraft.gametest.framework.GameTestHelper
import net.minecraft.gametest.framework.GameTestInfo
import net.minecraft.gametest.framework.GameTestSequence
import site.siredvin.testiarium.api.thenExecuteFailFast

fun GameTestSequence.thenStartComputer(name: String? = null, action: CctComputerAction): GameTestSequence {
    val test = testInfo()
    return thenExecuteFailFast { CctComputers.enqueue(test.level.server, test.label(name), action) }
}

fun GameTestSequence.thenOnComputer(name: String? = null, action: CctComputerAction): GameTestSequence {
    val test = testInfo()
    val label = test.label(name)
    lateinit var monitor: CctComputers.CctComputerMonitor
    thenExecuteFailFast { monitor = CctComputers.enqueue(test.level.server, label, action) }
    thenWaitUntil { if (!monitor.isFinished) throw GameTestAssertException("Computer '$label' has not finished yet") }
    return thenExecuteFailFast { monitor.check() }
}

fun GameTestSequence.thenComputerOk(name: String? = null, marker: String = CctComputerState.DONE): GameTestSequence {
    val label = testInfo().label(name)
    thenWaitUntil {
        if (CctComputerState.get(label)?.isDone(marker) != true) {
            throw GameTestAssertException("Computer '$label' has not reached $marker yet")
        }
    }
    return thenExecuteFailFast { CctComputerState.get(label)?.check(marker) ?: error("Computer '$label' disappeared") }
}

fun GameTestHelper.thenLua(label: String = testInfo().testName): GameTestSequence {
    CctLuaTests.require(label)
    return startSequence().thenComputerOk()
}

private fun GameTestInfo.label(name: String?) = testName + (name?.let { ".$it" } ?: "")

private fun GameTestSequence.testInfo(): GameTestInfo = javaClass.getDeclaredField("parent").let {
    it.isAccessible = true
    it.get(this) as GameTestInfo
}

private fun GameTestHelper.testInfo(): GameTestInfo = javaClass.getDeclaredField("testInfo").let {
    it.isAccessible = true
    it.get(this) as GameTestInfo
}
