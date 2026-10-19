package site.siredvin.tweakium.modules.player

import com.mojang.authlib.GameProfile
import net.fabricmc.fabric.api.entity.FakePlayer
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.player.Player

class FabricFakePlayer(serverLevel: ServerLevel, gameProfile: GameProfile) : FakePlayer(serverLevel, gameProfile) {
    override fun canHarmPlayer(other: Player): Boolean = true
    override fun die(damageSource: DamageSource) {}
    override fun getEyeY(): Double = y + 0.2
    override fun getAttackStrengthScale(f: Float): Float = 1f

    companion object {
        fun create(serverLevel: ServerLevel, profile: GameProfile): FabricFakePlayer {
            val currentPlayer = serverLevel.server.playerList.getPlayer(profile.id())
            val fakePlayer = FabricFakePlayer(serverLevel, profile)
            if (currentPlayer != null) fakePlayer.advancements.setPlayer(currentPlayer)
            return fakePlayer
        }
    }
}
