package site.siredvin.broccolium.test

import net.minecraft.core.Direction
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import org.junit.jupiter.api.Test
import site.siredvin.broccolium.modules.base.api.IConfigHandler
import site.siredvin.broccolium.modules.base.ext.fromRelative
import site.siredvin.broccolium.modules.base.ext.rotateX
import site.siredvin.broccolium.modules.base.ext.rotateY
import site.siredvin.broccolium.modules.base.ext.rotateZ
import site.siredvin.broccolium.modules.base.ext.toDiv16
import site.siredvin.broccolium.modules.base.ext.toMul16
import site.siredvin.broccolium.modules.base.ext.toRelative
import site.siredvin.broccolium.modules.base.util.TextBookUtils
import site.siredvin.broccolium.modules.base.util.ValueContainer
import site.siredvin.broccolium.modules.base.util.XPUtil
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BaseBehaviorTests {
    @Test
    fun experienceThresholdsMatchNativeLevelCosts() {
        var total = 0.0
        for (level in 0..100) {
            assertEquals(total, XPUtil.levelsToXP(level), "level $level")
            total += when {
                level >= 30 -> 9 * level - 158
                level >= 15 -> 5 * level - 38
                else -> 2 * level + 7
            }
        }
    }

    @Test
    fun experienceInverseIncludesExactThresholdAndExcludesNextThreshold() {
        for (level in 0..100) {
            val threshold = XPUtil.levelsToXP(level)
            assertEquals(level, XPUtil.xpToLevels(threshold), "exact threshold $threshold")
            assertEquals(level, XPUtil.xpToLevels(XPUtil.levelsToXP(level + 1) - 1), "before level ${level + 1}")
        }
    }

    @Test
    fun subtractingLevelsConservesTheReportedExperienceReduction() {
        for (level in listOf(1, 16, 17, 31, 32, 50)) {
            val xp = XPUtil.levelsToXP(level) + 0.5
            val remaining = XPUtil.subtractLevels(xp, 1)
            assertEquals(XPUtil.levelsToXP(level - 1), remaining)
            assertEquals(xp, remaining + XPUtil.levelReductionToXp(xp, 1))
        }
    }

    @Test
    fun modelBoxQuarterTurnsPreserveExtentsAndReturnAfterFourTurns() {
        val box = AABB(1.0, 2.0, 3.0, 5.0, 8.0, 12.0)
        for (rotate in listOf<(AABB) -> AABB>({ it.rotateX(1) }, { it.rotateY(1) }, { it.rotateZ(1) })) {
            var current = box
            repeat(4) { current = rotate(current) }
            assertEquals(box, current)
        }
        assertEquals(box, box.toDiv16().toMul16())
    }

    @Test
    fun relativeVectorsRoundTripWithoutLosingFractionalCoordinates() {
        val vector = Vec3(-4.25, 1.5, 7.75)
        for (direction in Direction.Plane.HORIZONTAL) {
            assertEquals(vector, vector.toRelative(direction).fromRelative(direction))
        }
        assertFailsWith<IllegalArgumentException> { vector.toRelative(Direction.UP) }
        assertFailsWith<IllegalArgumentException> { vector.fromRelative(Direction.DOWN) }
    }

    @Test
    fun configurationNamesAndPostfixRemainStable() {
        val handler = object : IConfigHandler {
            override val name = "SCAN_BLOCKS"
            override val settingsPostfix = "Operation"
        }
        assertEquals("scanBlocksOperation", handler.settingsName())
    }

    @Test
    fun valueContainerMutatesAndRejectsMissingRequiredValue() {
        val value = ValueContainer.of<String?>(null)
        assertTrue(value.isEmpty)
        assertFailsWith<NullPointerException> { value.notNull }
        value.mutate { "oak" }
        assertTrue(value.isPresent)
        assertFalse(value.isEmpty)
        assertEquals("oak", value.notNull)
    }

    @Test
    fun bookTextRespectsNewlineBudgetAndGlyphWidths() {
        assertEquals(1, TextBookUtils.getCharacterWidth('i'))
        assertEquals(6, TextBookUtils.getCharacterWidth('@'))
        assertEquals("line\n".repeat(14) + "line", TextBookUtils.stripText("line\n".repeat(16)))
        assertEquals("short text", TextBookUtils.stripText("short text"))
    }
}
