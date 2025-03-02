package site.siredvin.tweakium.modules.peripheral.blockentity

import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import site.siredvin.broccolium.modules.base.api.ISyncingBlockEntity
import site.siredvin.broccolium.modules.platform.PlatformToolkit
import site.siredvin.tweakium.modules.peripheral.api.IOwnedPeripheral
import java.util.*

abstract class MutablePeripheralBlockEntity<T : IOwnedPeripheral<*>>(
    blockEntityType: BlockEntityType<*>,
    blockPos: BlockPos,
    blockState: BlockState,
) : PeripheralBlockEntity<T>(blockEntityType, blockPos, blockState),
    ISyncingBlockEntity {
    open val updateFlag: Int
        get() = Block.UPDATE_ALL

    // Client-server sync logic

    override fun getUpdateTag(): CompoundTag {
        var base = super.getUpdateTag()
        base = saveInternalData(base)
        return base
    }

    override fun getUpdatePacket(): ClientboundBlockEntityDataPacket = ClientboundBlockEntityDataPacket.create(this)

    // Data save logic

    override fun load(compound: CompoundTag) {
        super.load(compound)
        loadInternalData(compound)
    }

    override fun saveAdditional(compound: CompoundTag) {
        var tag: CompoundTag = compound
        tag = saveInternalData(tag)
        return super.saveAdditional(tag)
    }

    // Server->client sync logic

    override fun pushInternalDataChangeToClient(state: BlockState?) {
        val level = getLevel() ?: return
        val realState = state ?: blockState
        Objects.requireNonNull<Any?>(level)
        if (!level.isClientSide) {
            setChanged()
            level.setBlockAndUpdate(blockPos, realState)
            level.sendBlockUpdated(blockPos, realState, realState, updateFlag)
        }
    }

    // Render tricks

    override fun triggerRenderUpdate() {
        PlatformToolkit.get().triggerRenderUpdate(this)
    }
}
