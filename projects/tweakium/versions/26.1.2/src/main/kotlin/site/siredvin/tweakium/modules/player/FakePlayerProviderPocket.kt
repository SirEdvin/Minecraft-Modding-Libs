package site.siredvin.tweakium.modules.player

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.pocket.IPocketAccess
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer

import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import site.siredvin.tweakium.modules.platform.ComputerPlatformToolkit
import java.util.concurrent.TimeUnit
import java.util.function.Function
import java.util.function.Supplier

object FakePlayerProviderPocket {
    private val registeredPlayers = CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.MINUTES).weakKeys()
        .maximumSize(3_000).build(CacheLoader.from(::buildPlayer))

    private fun buildPlayer(pocket: IPocketAccess): FakePlayerProxy = FakePlayerProxy(ComputerPlatformToolkit.get().createFakePlayer(pocket.level as ServerLevel, (pocket.entity as? ServerPlayer)?.gameProfile ?: FakePlayerProxy.DUMMY_PROFILE))

    private fun load(player: ServerPlayer, realPlayer: Player, overwrittenDirection: Direction? = null, skipInventory: Boolean = false) {
        val direction = overwrittenDirection ?: realPlayer.direction
        player.setServerLevel(realPlayer.level() as ServerLevel)
        val position = realPlayer.blockPosition()
        val pitch = when (direction) {
            Direction.UP -> -90f
            Direction.DOWN -> 90f
            else -> realPlayer.xRot
        }
        val yaw = when (direction) {
            Direction.SOUTH -> 0f
            Direction.WEST -> 90f
            Direction.NORTH -> 180f
            Direction.EAST -> -90f
            else -> realPlayer.yRot
        }
        val a = direction.axis
        val ad = direction.axisDirection
        val x = if (a === Direction.Axis.X && ad == Direction.AxisDirection.NEGATIVE) -.5 else .5 + direction.stepX / 1.9
        val y = 0.5 + direction.stepY / 1.9
        val z = if (a === Direction.Axis.Z && ad == Direction.AxisDirection.NEGATIVE) -.5 else .5 + direction.stepZ / 1.9
        player.snapTo(position.x + x, position.y + y, position.z + z, yaw, pitch)
        if (!skipInventory) {
            val playerInventory: Inventory = player.inventory
            playerInventory.selectedSlot = 0
            val realPlayerInventory = realPlayer.inventory
            val size = realPlayerInventory.containerSize
            for (i in 0 until size) playerInventory.setItem(i, realPlayerInventory.getItem(i))
            FakePlayerSessionUtil.swapSelected(playerInventory, realPlayerInventory.selectedSlot)
        }
    }

    private fun unload(player: ServerPlayer, realPlayer: Player, skipInventory: Boolean = false) {
        val playerInventory: Inventory = player.inventory
        playerInventory.selectedSlot = 0

        if (!skipInventory) {
            val realPlayerInventory = realPlayer.inventory
            val size = realPlayerInventory.containerSize
            FakePlayerSessionUtil.swapSelected(playerInventory, realPlayerInventory.selectedSlot)
            for (i in 0 until size) {
                realPlayerInventory.setItem(i, playerInventory.getItem(i))
                playerInventory.setItem(i, ItemStack.EMPTY)
            }
        }
    }

    fun <T> withPlayer(pocket: IPocketAccess, function: Function<FakePlayerProxy, T>, overwrittenDirection: Direction? = null, skipInventory: Boolean = false): T {
        val realPlayer = pocket.entity as? Player ?: throw LuaException("Cannot init player for this pocket computer for some reason")
        val player: FakePlayerProxy = registeredPlayers.get(pocket)
        load(player.fakePlayer, realPlayer, overwrittenDirection, skipInventory)
        try {
            return FakePlayerSessionUtil.applyToolAttributes(if (skipInventory) ItemStack.EMPTY else player.fakePlayer.mainHandItem, player.fakePlayer.attributes).use {
                function.apply(player)
            }
        } finally {
            unload(player.fakePlayer, realPlayer, skipInventory)
        }
    }

    fun <T> withPlayerTweaked(pocket: IPocketAccess, function: Function<FakePlayerProxy, T>, playerProvider: Supplier<FakePlayerProxy>, overwrittenDirection: Direction? = null, skipInventory: Boolean = false): T {
        val realPlayer = pocket.entity as? Player ?: throw LuaException("Cannot init player for this pocket computer for some reason")
        val player = playerProvider.get()
        load(player.fakePlayer, realPlayer, overwrittenDirection, skipInventory)
        try {
            return FakePlayerSessionUtil.applyToolAttributes(if (skipInventory) ItemStack.EMPTY else player.fakePlayer.mainHandItem, player.fakePlayer.attributes).use {
                function.apply(player)
            }
        } finally {
            unload(player.fakePlayer, realPlayer, skipInventory)
        }
    }
}
