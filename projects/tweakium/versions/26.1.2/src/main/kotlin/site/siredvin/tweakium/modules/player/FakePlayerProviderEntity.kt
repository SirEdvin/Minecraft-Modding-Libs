package site.siredvin.tweakium.modules.player

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.ItemStack
import site.siredvin.broccolium.modules.storage.base.api.SlottedAgnosticStorage
import site.siredvin.broccolium.modules.storage.item.AgnosticItemStorageLookup
import site.siredvin.tweakium.modules.platform.ComputerPlatformToolkit
import java.util.concurrent.TimeUnit
import java.util.function.Function

object FakePlayerProviderEntity {
    private val registeredPlayers = CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.MINUTES).weakKeys()
        .maximumSize(3_000).build(CacheLoader.from(::buildPlayer))

    private fun buildPlayer(pair: Pair<Entity, ServerPlayer>): FakePlayerProxy = FakePlayerProxy(ComputerPlatformToolkit.get().createFakePlayer(pair.first.level() as ServerLevel, pair.second.gameProfile))

    fun <T> withPlayer(entity: Entity, realPlayer: ServerPlayer, function: Function<FakePlayerProxy, T>, overwrittenDirection: Direction? = null, skipInventory: Boolean = false): T {
        val player: FakePlayerProxy = registeredPlayers.get(Pair(entity, realPlayer))
        val storage = AgnosticItemStorageLookup.extractFromEntity(entity.level(), entity, null) as? SlottedAgnosticStorage<ItemStack, Int>
        if (!skipInventory && storage == null) {
            throw IllegalArgumentException("Cannot init fake player with storage and with block entity without storage")
        }
        return FakePlayerStorageSession.withPlayer(player, realPlayer, storage, function, overwrittenDirection, skipInventory)
    }
}
