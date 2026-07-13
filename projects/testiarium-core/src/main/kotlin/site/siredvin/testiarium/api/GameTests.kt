// SPDX-FileCopyrightText: 2022 The CC: Tweaked Developers
// SPDX-FileCopyrightText: 2026 SirEdvin
// SPDX-License-Identifier: MPL-2.0
// Adapted from CC:Tweaked projects/common/src/testMod/kotlin/dan200/computercraft/gametest/api/ClientGameTest.kt and TestTags.kt

package site.siredvin.testiarium.api

import net.minecraft.gametest.framework.GameTest
import net.minecraft.gametest.framework.GameTestHelper
import net.minecraft.gametest.framework.GameTestSequence

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ClientGameTest(
    val template: String = "",
    val timeoutTicks: Int = Timeouts.DEFAULT,
)

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class TestGroup(val value: String)

object TestTags {
    const val COMMON = "common"
    const val CLIENT = "client"

    private val enabled = System.getProperty("testiarium.tags", COMMON).split(',').toSet()

    fun isEnabled(tag: String) = tag in enabled
}

object Timeouts {
    const val SECOND = 20
    const val DEFAULT = SECOND * 5
}

fun GameTestHelper.sequence(actions: GameTestSequence.() -> Unit) {
    startSequence().apply(actions).thenSucceed()
}

fun GameTestHelper.immediate(action: () -> Unit) {
    action()
    succeed()
}
