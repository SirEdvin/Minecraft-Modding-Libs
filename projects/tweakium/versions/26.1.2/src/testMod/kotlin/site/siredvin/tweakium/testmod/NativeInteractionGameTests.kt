package site.siredvin.tweakium.testmod

import com.mojang.authlib.GameProfile
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.gametest.framework.GameTestHelper
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3
import site.siredvin.testiarium.api.GameTest
import site.siredvin.testiarium.api.TestGroup
import site.siredvin.testiarium.api.immediate
import site.siredvin.tweakium.modules.platform.ComputerPlatformToolkit
import site.siredvin.tweakium.modules.player.FakePlayerProxy
import java.util.UUID

object NativeInteractionHooks {
    const val PLAYER = "R3Interaction"
    var uses = 0
    var deny = false
}

@TestGroup("tweakium")
class NativeInteractionGameTests {
    @GameTest(template = "modulebehavior.empty")
    fun oneNativeUseAndLoaderCancellationAndBreakProtectionArePreserved(helper: GameTestHelper) = helper.immediate {
        val pos = helper.absolutePos(BlockPos(1, 1, 2))
        val player = ComputerPlatformToolkit.get().createFakePlayer(helper.level, GameProfile(UUID.randomUUID(), NativeInteractionHooks.PLAYER))
        player.setPos(pos.x + 0.5, pos.y + 0.5, pos.z - 1.5)
        player.yRot = 0f
        player.xRot = 0f
        val proxy = FakePlayerProxy(player)
        helper.level.setBlockAndUpdate(pos, Blocks.STONE.defaultBlockState())
        NativeInteractionHooks.uses = 0
        NativeInteractionHooks.deny = false
        try {
            player.inventory.setItem(0, ItemStack(Items.STICK))
            proxy.use(true, false)
            check(NativeInteractionHooks.uses == 1) { "One fake-player action dispatched ${NativeInteractionHooks.uses} native right-click hooks" }
            NativeInteractionHooks.uses = 0
            NativeInteractionHooks.deny = true
            player.inventory.setItem(0, ItemStack(Items.DIAMOND_BLOCK, 2))
            proxy.use(true, false)
            check(NativeInteractionHooks.uses == 1) { "Cancelled interaction was retried" }
            check(player.mainHandItem.count == 2 && helper.level.isEmptyBlock(pos.north())) { "Cancelled interaction mutated inventory/world" }
            val result = proxy.swingBlock(BlockHitResult(Vec3.atLowerCornerOf(pos), Direction.NORTH, pos, false))
            check(!result.first && result.second == "Cannot break protected block")
            check(helper.level.getBlockState(pos).`is`(Blocks.STONE))
        } finally {
            NativeInteractionHooks.deny = false
            player.inventory.clearContent()
            player.discard()
        }
    }
}
