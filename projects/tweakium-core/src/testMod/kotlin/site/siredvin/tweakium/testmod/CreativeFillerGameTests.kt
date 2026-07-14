package site.siredvin.tweakium.testmod

import net.minecraft.gametest.framework.GameTest
import net.minecraft.gametest.framework.GameTestHelper
import net.minecraft.gametest.framework.GameTestAssertException
import net.minecraft.world.Container
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import site.siredvin.testiarium.api.TestGroup
import site.siredvin.testiarium.cct.thenLua

@TestGroup("tweakium")
class CreativeFillerGameTests {
    @GameTest(template = "creativefillergametests.fillsinventory")
    fun fillsInventory(helper: GameTestHelper) = helper.thenLua()
        .thenExecute {
            val center = helper.absolutePos(net.minecraft.core.BlockPos(2, 2, 2))
            val target = (-2..2).asSequence().flatMap { x ->
                (-2..2).asSequence().flatMap { y ->
                    (-2..2).asSequence().map { z -> helper.level.getBlockEntity(center.offset(x, y, z)) }
                }
            }.filterIsInstance<Container>().singleOrNull()
                ?: throw GameTestAssertException("Expected one container near $center")
            if (!ItemStack.matches(target.getItem(0), ItemStack(Items.STONE))) {
                throw GameTestAssertException("Expected the fixture target to contain stone, got ${target.getItem(0)}")
            }
        }
        .thenSucceed()
}
