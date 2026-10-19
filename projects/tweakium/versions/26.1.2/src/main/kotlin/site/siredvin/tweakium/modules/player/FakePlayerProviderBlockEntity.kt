package site.siredvin.tweakium.modules.player

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import dan200.computercraft.api.lua.LuaException
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import site.siredvin.broccolium.modules.base.api.IOwnedBlockEntity
import site.siredvin.broccolium.modules.storage.base.api.SlottedAgnosticStorage
import site.siredvin.broccolium.modules.storage.item.AgnosticItemStorageLookup
import site.siredvin.tweakium.modules.platform.ComputerPlatformToolkit
import java.util.concurrent.TimeUnit
import java.util.function.Function

object FakePlayerProviderBlockEntity {
    private val registeredPlayers = CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.MINUTES).weakKeys()
        .maximumSize(3_000).build(CacheLoader.from(::buildPlayer))

    private fun buildPlayer(blockEntity: BlockEntity): FakePlayerProxy {
        if (blockEntity !is IOwnedBlockEntity || blockEntity.player == null) {
            throw IllegalArgumentException("Cannot use fake player logic without owned block entity")
        }
        return FakePlayerProxy(ComputerPlatformToolkit.get().createFakePlayer(blockEntity.level as ServerLevel, blockEntity.player!!.gameProfile))
    }

    fun <T> withPlayer(blockEntity: BlockEntity, function: Function<FakePlayerProxy, T>, overwrittenDirection: Direction? = null, skipInventory: Boolean = false): T {
        if (blockEntity !is IOwnedBlockEntity || blockEntity.player == null) {
            throw IllegalArgumentException("Cannot use fake player logic without owned block entity")
        }
        val realPlayer = blockEntity.player ?: throw LuaException("Cannot init player for this block entity computer for some reason")
        if (registeredPlayers.getIfPresent(blockEntity)?.fakePlayer?.gameProfile?.let { it != realPlayer.gameProfile } == true) registeredPlayers.invalidate(blockEntity)
        val player: FakePlayerProxy = registeredPlayers.get(blockEntity)
        val storage = AgnosticItemStorageLookup.extractFromBlock(blockEntity.level!!, blockEntity.blockPos, blockEntity, null) as? SlottedAgnosticStorage<ItemStack, Int>
        if (!skipInventory && storage == null) {
            throw IllegalArgumentException("Cannot init fake player with storage and with block entity without storage")
        }
        return FakePlayerStorageSession.withPlayer(player, realPlayer, storage, function, overwrittenDirection, skipInventory)
    }
}
