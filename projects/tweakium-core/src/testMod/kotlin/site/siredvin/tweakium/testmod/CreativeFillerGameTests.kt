package site.siredvin.tweakium.testmod

import net.minecraft.gametest.framework.GameTest
import net.minecraft.gametest.framework.GameTestHelper
import site.siredvin.testiarium.api.TestGroup
import site.siredvin.testiarium.cct.thenLua

@TestGroup("tweakium")
class CreativeFillerGameTests {
    @GameTest(template = "creativefillergametests.fillsinventory")
    fun fillsInventory(helper: GameTestHelper) = helper.thenLua().thenSucceed()
}
