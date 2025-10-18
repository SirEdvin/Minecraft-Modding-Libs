package site.siredvin.tweakium.modules.peripheral.owner

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.properties.DirectionProperty
import site.siredvin.broccolium.modules.base.api.IOwnedBlockEntity
import site.siredvin.broccolium.modules.base.block.FacingBlockEntityBlock
import site.siredvin.broccolium.modules.storage.item.AgnosticItemStorageLookup
import site.siredvin.broccolium.modules.storage.item.api.SlottedAgnosticItemStorage
import site.siredvin.tweakium.modules.peripheral.api.IDataStorage
import site.siredvin.tweakium.modules.peripheral.util.CompoundTagDataStorage
import site.siredvin.tweakium.modules.player.FakePlayerProviderBlockEntity
import site.siredvin.tweakium.modules.player.FakePlayerProxy
import java.util.*

open class RawBlockEntityPeripheralOwner<T>(val blockEntity: T, val facingProperty: DirectionProperty = FacingBlockEntityBlock.FACING) : BasePeripheralOwner() where T : BlockEntity {

    override val level: Level?
        get() = Objects.requireNonNull(blockEntity.level)
    override val pos: BlockPos
        get() = blockEntity.blockPos

    override val targetRepresentation: T
        get() = blockEntity

    override val facing: Direction
        get() {
            val state = blockEntity.blockState
            if (state.hasProperty(facingProperty)) return state.getValue(facingProperty)
            return Direction.NORTH
        }

    override val ownerUUID: UUID?
        get() = (blockEntity as? IOwnedBlockEntity)?.ownerPlayerUUID
    override val owner: Player?
        get() = (blockEntity as? IOwnedBlockEntity)?.player

    override val dataStorage: IDataStorage by lazy {
        CompoundTagDataStorage(CompoundTag()) {}
    }

    override val storage: SlottedAgnosticItemStorage? by lazy {
        AgnosticItemStorageLookup.extractStorage(blockEntity.level!!, blockEntity.blockPos, blockEntity) as? SlottedAgnosticItemStorage
    }

    override fun <T> withPlayer(function: (FakePlayerProxy) -> T, overwrittenDirection: Direction?, skipInventory: Boolean): T {
        if (blockEntity !is IOwnedBlockEntity) {
            throw IllegalArgumentException("Cannot perform player logic without owned block entity")
        }
        return FakePlayerProviderBlockEntity.withPlayer(blockEntity, function, overwrittenDirection = overwrittenDirection, skipInventory = skipInventory)
    }

    override val toolInMainHand: ItemStack
        get() = ItemStack.EMPTY

    override fun storeItem(stored: ItemStack): ItemStack {
        if (storage == null) {
            return stored
        }
        return storage!!.storeItem(stored)
    }

    override fun destroyUpgrade() {
        level!!.removeBlock(blockEntity.blockPos, false)
    }

    override fun isMovementPossible(level: Level, pos: BlockPos): Boolean = false

    override fun move(level: Level, pos: BlockPos): Boolean = false

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RawBlockEntityPeripheralOwner<*>) return false
        if (!super.equals(other)) return false

        if (blockEntity != other.blockEntity) return false
        return facingProperty == other.facingProperty
    }

    override fun hashCode(): Int {
        var result = blockEntity.hashCode()
        result = 31 * result + facingProperty.hashCode()
        return result
    }
}
