package site.siredvin.testiarium

import net.minecraft.gametest.framework.GameTestInstance
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal fun assertVersionMetadata(test: GameTestInstance) {
    assertTrue(test.manualOnly())
    assertTrue(test.skyAccess())
    assertEquals(3, test.padding())
}
