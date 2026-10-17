package site.siredvin.tweakium.modules.player

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import dan200.computercraft.api.turtle.ITurtleAccess
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer

import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.item.ItemStack
import site.siredvin.broccolium.modules.storage.item.ContainerUtils
import site.siredvin.tweakium.modules.platform.ComputerPlatformToolkit
import java.util.concurrent.TimeUnit
import java.util.function.Function

object FakePlayerProviderTurtle {
    private val registeredPlayers = CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.MINUTES).weakKeys()
        .maximumSize(3_000).build(CacheLoader.from(::buildPlayer))

    private fun buildPlayer(turtle: ITurtleAccess): FakePlayerProxy = FakePlayerProxy(ComputerPlatformToolkit.get().createFakePlayer(turtle.level as ServerLevel, turtle.owningPlayer ?: FakePlayerProxy.DUMMY_PROFILE))

    private fun load(player: ServerPlayer, turtle: ITurtleAccess, overwrittenDirection: Direction? = null, skipInventory: Boolean = false) {
        val direction = overwrittenDirection ?: turtle.direction
        player.setServerLevel(turtle.level as ServerLevel)
        val position = turtle.position
        val pitch = when (direction) {
            Direction.UP -> -90f
            Direction.DOWN -> 90f
            else -> 0f
        }
        val yaw = when (direction) {
            Direction.SOUTH -> 0f
            Direction.WEST -> 90f
            Direction.NORTH -> 180f
            else -> -90f
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
            val turtleInventory = turtle.inventory
            val size = turtleInventory.containerSize
            val largerSize = playerInventory.containerSize
            for (i in 0 until size) playerInventory.setItem(i, turtleInventory.getItem(i))
            for (i in size until largerSize) playerInventory.setItem(i, ItemStack.EMPTY)
            FakePlayerSessionUtil.swapSelected(playerInventory, turtle.selectedSlot)
        }
    }

    private fun unload(player: ServerPlayer, turtle: ITurtleAccess, selectedSlot: Int, skipInventory: Boolean = false) {
        val playerInventory: Inventory = player.inventory
        playerInventory.selectedSlot = 0

        if (!skipInventory) {
            val turtleInventory = turtle.inventory
            val size = turtleInventory.containerSize
            val largerSize = playerInventory.containerSize
            FakePlayerSessionUtil.swapSelected(playerInventory, selectedSlot)
            for (i in 0 until size) {
                turtleInventory.setItem(i, playerInventory.getItem(i))
                playerInventory.setItem(i, ItemStack.EMPTY)
            }
            for (i in size until largerSize) {
                val remaining = playerInventory.getItem(i)
                if (!remaining.isEmpty) ContainerUtils.toInventoryOrToWorld(remaining, turtleInventory, 0, turtle.position, turtle.level)
                playerInventory.setItem(i, ItemStack.EMPTY)
            }
        }
    }

    fun <T> withPlayer(turtle: ITurtleAccess, function: Function<FakePlayerProxy, T>, overwrittenDirection: Direction? = null, skipInventory: Boolean = false): T {
        val profile = turtle.owningPlayer ?: FakePlayerProxy.DUMMY_PROFILE
        if (registeredPlayers.getIfPresent(turtle)?.fakePlayer?.gameProfile?.let { it != profile } == true) registeredPlayers.invalidate(turtle)
        val player: FakePlayerProxy = registeredPlayers.get(turtle)
        val selectedSlot = turtle.selectedSlot
        load(player.fakePlayer, turtle, overwrittenDirection, skipInventory)
        try {
            return FakePlayerSessionUtil.applyToolAttributes(if (skipInventory) ItemStack.EMPTY else player.fakePlayer.mainHandItem, player.fakePlayer.attributes).use {
                function.apply(player)
            }
        } finally {
            unload(player.fakePlayer, turtle, selectedSlot, skipInventory)
        }
    }
}
