package site.siredvin.tweakium.testmod

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.minecraft.world.InteractionResult

object FabricInteractionFixtures {
    fun install() {
        UseBlockCallback.EVENT.register { player, _, _, _ ->
            if (player.gameProfile.name() == NativeInteractionHooks.PLAYER) {
                NativeInteractionHooks.uses++
                if (NativeInteractionHooks.deny) InteractionResult.FAIL else InteractionResult.PASS
            } else {
                InteractionResult.PASS
            }
        }
        PlayerBlockBreakEvents.BEFORE.register { _, player, _, _, _ ->
            player.gameProfile.name() != NativeInteractionHooks.PLAYER || !NativeInteractionHooks.deny
        }
    }
}
