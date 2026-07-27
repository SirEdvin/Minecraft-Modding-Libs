package site.siredvin.tweakium.modules.player

import com.mojang.authlib.GameProfile
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.common.util.FakePlayer

open class ForgeFakePlayer(level: ServerLevel, profile: GameProfile) : FakePlayer(level, profile) {

    override fun canHarmPlayer(other: Player): Boolean = true

    override fun die(damageSource: DamageSource) {}

    override fun getEyeY(): Double = y + 0.2

    override fun getAttackStrengthScale(f: Float): Float = 1f
}
