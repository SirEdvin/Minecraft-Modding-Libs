package site.siredvin.broccolium.modules.base.util

import kotlin.math.pow
import kotlin.math.sqrt

object XPUtil {
    private const val REQUIRED_FOR_16 = 352
    private const val REQUIRED_FOR_31 = 1507

    fun xpToLevels(xp: Double): Int {
        if (xp > REQUIRED_FOR_31) {
            return ((162.5 + sqrt(116.5 * 116.5 + 18.0 * (xp - REQUIRED_FOR_31))) / 9.0).toInt()
        }
        if (xp > REQUIRED_FOR_16) {
            return ((40.5 + sqrt(39.5 * 39.5 + 10.0 * (xp - REQUIRED_FOR_16))) / 5.0).toInt()
        }
        return (sqrt(xp + 9) - 3).toInt()
    }

    fun levelsToXP(level: Int): Double {
        if (level > 31) {
            return 4.5 * level.toDouble().pow(2.0) - 162.5 * level + 2220
        }
        if (level > 16) {
            return 2.5 * level.toDouble().pow(2.0) - 40.5 * level + 360
        }
        return level.toDouble().pow(2.0) + 6 * level
    }

    fun levelReductionToXp(xp: Double, amount: Int): Double = xp - levelsToXP(xpToLevels(xp) - amount)

    fun subtractLevels(xp: Double, amount: Int): Double = levelsToXP(xpToLevels(xp) - amount)
}
