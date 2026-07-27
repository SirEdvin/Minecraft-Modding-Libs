package site.siredvin.tweakium.modules.peripheral.owner

import dan200.computercraft.api.pocket.IPocketAccess
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import site.siredvin.broccolium.modules.storage.base.api.SlottedAgnosticStorage
import site.siredvin.broccolium.modules.storage.item.ContainerUtils
import site.siredvin.broccolium.modules.storage.item.ContainerWrapper
import site.siredvin.tweakium.modules.peripheral.api.IDataStorage
import site.siredvin.tweakium.modules.peripheral.boon.PeripheralOwnerBoonKey
import site.siredvin.tweakium.modules.peripheral.boon.PocketFuelBoon
import site.siredvin.tweakium.modules.peripheral.util.DataStorageUtil
import site.siredvin.tweakium.modules.player.FakePlayerProviderPocket
import site.siredvin.tweakium.modules.player.FakePlayerProxy

open class PocketPeripheralOwner(val pocket: IPocketAccess) : BasePeripheralOwner() {
    override val level: Level?
        get() {
            val owner = pocket.entity ?: return null
            return owner.commandSenderWorld
        }
    override val pos: BlockPos
        get() {
            val owner = pocket.entity ?: return BlockPos(0, 0, 0)
            return owner.blockPosition()
        }
    override val facing: Direction
        get() {
            val owner = pocket.entity ?: return Direction.NORTH
            return owner.direction
        }
    override val owner: Player?
        get() = pocket.entity as? Player

    override val dataStorage: IDataStorage
        get() = DataStorageUtil.getDataStorage(pocket)

    override val storage: SlottedAgnosticStorage<ItemStack, Int>?
        get() = owner?.inventory?.let { ContainerWrapper(it) }

    override fun <T> withPlayer(function: (FakePlayerProxy) -> T, overwrittenDirection: Direction?, skipInventory: Boolean): T = FakePlayerProviderPocket.withPlayer(pocket, function, overwrittenDirection = overwrittenDirection, skipInventory = skipInventory)

    override val toolInMainHand: ItemStack
        get() = owner?.mainHandItem ?: ItemStack.EMPTY

    override fun storeItem(stored: ItemStack): ItemStack {
        val player = owner ?: return stored
        return ContainerUtils.storeItem(player.inventory, stored, simulate = false)
    }

    override fun destroyUpgrade(): Unit = throw RuntimeException("Not implemented yet")

    override fun isMovementPossible(level: Level, pos: BlockPos): Boolean = false

    override fun move(level: Level, pos: BlockPos): Boolean = false

    fun attachFuel(foodFuelPrice: Int = 1000, maxFuelConsumptionLevel: Int = 1): PocketPeripheralOwner {
        attachBoon(PeripheralOwnerBoonKey.FUEL, PocketFuelBoon(this, foodFuelPrice, maxFuelConsumptionLevel))
        return this
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PocketPeripheralOwner) return false
        if (!super.equals(other)) return false

        return pocket == other.pocket
    }

    override fun hashCode(): Int = pocket.hashCode()

    override val targetRepresentation: Any?
        get() = owner
}
