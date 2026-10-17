package site.siredvin.tweakium.modules.player

import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import site.siredvin.broccolium.modules.storage.base.api.SlottedAgnosticStorage
import site.siredvin.broccolium.modules.storage.item.ItemStorageUtils
import java.util.function.Function

internal object FakePlayerStorageSession {
    fun <T> withPlayer(proxy: FakePlayerProxy, realPlayer: Player, storage: SlottedAgnosticStorage<ItemStack, Int>?, function: Function<FakePlayerProxy, T>, overwrittenDirection: Direction?, skipInventory: Boolean): T {
        val player = proxy.fakePlayer
        load(player, realPlayer, storage, overwrittenDirection, skipInventory)
        try {
            return FakePlayerSessionUtil.applyToolAttributes(if (skipInventory) ItemStack.EMPTY else player.mainHandItem, player.attributes).use {
                function.apply(proxy)
            }
        } finally {
            unload(player, realPlayer, storage, skipInventory)
        }
    }

    private fun load(player: ServerPlayer, realPlayer: Player, storage: SlottedAgnosticStorage<ItemStack, Int>?, overwrittenDirection: Direction?, skipInventory: Boolean) {
        val direction = overwrittenDirection ?: realPlayer.direction
        player.setServerLevel(realPlayer.level() as ServerLevel)
        val position = realPlayer.blockPosition()
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
        if (!skipInventory && storage != null) {
            val inventory = player.inventory
            inventory.selectedSlot = 0
            FakePlayerSessionUtil.borrowInventory(storage, inventory) { remaining ->
                ItemStorageUtils.toInventoryOrToWorld(remaining, storage, 0, realPlayer.blockPosition(), realPlayer.level())
            }
        }
    }

    private fun unload(player: ServerPlayer, realPlayer: Player, storage: SlottedAgnosticStorage<ItemStack, Int>?, skipInventory: Boolean) {
        val inventory = player.inventory
        inventory.selectedSlot = 0
        if (!skipInventory && storage != null) {
            FakePlayerSessionUtil.returnInventory(storage, inventory) { remaining ->
                ItemStorageUtils.toInventoryOrToWorld(remaining, storage, 0, realPlayer.blockPosition(), realPlayer.level())
            }
        }
    }
}
