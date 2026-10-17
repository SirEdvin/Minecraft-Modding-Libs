package site.siredvin.tweakium.testmod

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
//? if <26.1 {
import net.minecraft.gametest.framework.GameTest
//?} else {
/*import site.siredvin.testiarium.api.GameTest
*///?}
import net.minecraft.gametest.framework.GameTestHelper
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import site.siredvin.broccolium.modules.base.util.world.ScanUtils
import site.siredvin.testiarium.api.TestGroup
import site.siredvin.testiarium.api.immediate
import site.siredvin.tweakium.modules.minecraft.blockentity.CreativeFillerBlockEntity
import site.siredvin.tweakium.modules.minecraft.setup.TweakiumBlocks
import site.siredvin.tweakium.modules.peripheral.representation.LuaRepresentation
import site.siredvin.tweakium.modules.peripheral.representation.RepresentationMode
import site.siredvin.tweakium.modules.peripheral.util.DataStorageUtil
import java.util.UUID

@TestGroup("tweakium")
class WorldBehaviorGameTests {
    @GameTest(template = "modulebehavior.empty")
    fun blockTraversalHonorsBoundaryPredicateAndRelativeCoordinates(helper: GameTestHelper) = helper.immediate {
        val center = helper.absolutePos(BlockPos(1, 2, 1))
        val inside = center.offset(1, 0, 0)
        val outside = center.offset(2, 0, 0)
        helper.level.setBlockAndUpdate(inside, Blocks.DIAMOND_BLOCK.defaultBlockState())
        helper.level.setBlockAndUpdate(outside, Blocks.DIAMOND_BLOCK.defaultBlockState())
        val absolute = mutableListOf<BlockPos>()
        ScanUtils.traverseBlocks(helper.level, center, 1, { _, pos -> absolute += pos }, predicate = { it.`is`(Blocks.DIAMOND_BLOCK) })
        check(absolute == listOf(inside))
        val relative = mutableListOf<BlockPos>()
        ScanUtils.traverseBlocks(helper.level, center, 1, { _, pos -> relative += pos }, relativePosition = true, predicate = { it.`is`(Blocks.DIAMOND_BLOCK) })
        check(relative == listOf(BlockPos(1, 0, 0)))
        val zeroRadius = mutableListOf<BlockPos>()
        ScanUtils.traverseBlocks(helper.level, center, 0, { _, pos -> zeroRadius += pos })
        check(zeroRadius.isEmpty()) { "Default traversal must exclude air" }
    }

    @GameTest(template = "modulebehavior.empty")
    fun luaRepresentationsExposeNativeCountsAndBlockProperties(helper: GameTestHelper) = helper.immediate {
        val stack = ItemStack(Items.DIAMOND, 7)
        for (mode in RepresentationMode.values()) {
            val represented = LuaRepresentation.forItemStack(stack, mode)
            check(represented["name"] == "minecraft:diamond")
            check((represented["count"] as Number).toInt() == 7)
            check(stack.count == 7) { "Representation must not consume its input" }
        }
        val state = Blocks.OAK_STAIRS.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST)
        val represented = LuaRepresentation.forBlockState(state)
        check(represented["name"] == "minecraft:oak_stairs")
        check((represented["state"] as Map<*, *>)["facing"] == "west")
        check(LuaRepresentation.forItem(Items.DIAMOND)["count"] == null)
    }

    @GameTest(template = "modulebehavior.empty")
    fun registeredPeripheralPreservesOwnerAndSettingsAcrossNativeNbtReload(helper: GameTestHelper) = helper.immediate {
        val position = BlockPos(1, 1, 1)
        helper.setBlock(position, TweakiumBlocks.CREATIVE_FILLER.get())
        val block = helper.level.getBlockEntity(helper.absolutePos(position)) as CreativeFillerBlockEntity
        val owner = UUID.fromString("1da2d610-d080-4f35-9650-7c4c68d595c1")
        block.ownerPlayerUUID = owner
        DataStorageUtil.getDataStorage(block).putInt("limit", 42)
        val first = block.getPeripheral(Direction.NORTH)
        check(first === block.getPeripheral(Direction.SOUTH)) { "A block must reuse its peripheral instance" }
        check(block.connectedComputers.isEmpty())
        val restored = CreativeFillerBlockEntity(block.blockPos, block.blockState)
        reloadCreativeFiller(block, restored, helper)
        check(restored.ownerPlayerUUID == owner)
        check(DataStorageUtil.getDataStorage(restored).getInt("limit") == 42)
        DataStorageUtil.getDataStorage(restored).putInt("limit", 9)
        check(DataStorageUtil.getDataStorage(block).getInt("limit") == 42)
        check(restored.getPeripheral(Direction.NORTH) !== first)
    }
}
