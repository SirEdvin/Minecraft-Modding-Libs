package site.siredvin.tweakium.testmod

import net.minecraft.world.InteractionResult
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent
import net.neoforged.neoforge.event.level.block.BreakBlockEvent

object ForgeInteractionFixtures {
    @JvmStatic
    fun install() {
        NativeUpgradeFixtures.configurePlayer = java.util.function.Consumer { player ->
            net.neoforged.neoforge.network.registration.NetworkRegistry.configureMockConnection(player.connection.getConnection())
        }
        NeoForge.EVENT_BUS.addListener { event: PlayerInteractEvent.RightClickBlock ->
            if (event.entity.gameProfile.name() == NativeInteractionHooks.PLAYER) {
                NativeInteractionHooks.uses++
                if (NativeInteractionHooks.deny) {
                    event.isCanceled = true
                    event.cancellationResult = InteractionResult.FAIL
                }
            }
        }
        NeoForge.EVENT_BUS.addListener { event: BreakBlockEvent ->
            if (event.player.gameProfile.name() == NativeInteractionHooks.PLAYER && NativeInteractionHooks.deny) event.isCanceled = true
        }
    }
}
