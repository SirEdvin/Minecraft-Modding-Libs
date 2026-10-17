package site.siredvin.tweakium.modules.peripheral.blockentity

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import site.siredvin.broccolium.modules.base.api.ISyncingBlockEntity
import site.siredvin.broccolium.modules.platform.PlatformToolkit
import site.siredvin.tweakium.modules.peripheral.api.IOwnedPeripheral

abstract class MutablePeripheralBlockEntity<T : IOwnedPeripheral<*>>(
    blockEntityType: BlockEntityType<*>,
    blockPos: BlockPos,
    blockState: BlockState,
) : PeripheralBlockEntity<T>(blockEntityType, blockPos, blockState),
    ISyncingBlockEntity {
    companion object {
        private val INTERNAL_DATA_CODEC: MapCodec<CompoundTag> = MapCodec.assumeMapUnsafe(CompoundTag.CODEC)
    }

    open val updateFlag: Int
        get() = Block.UPDATE_ALL

    override fun getUpdateTag(provider: HolderLookup.Provider): CompoundTag = saveInternalData(super.getUpdateTag(provider))

    override fun getUpdatePacket(): ClientboundBlockEntityDataPacket = ClientboundBlockEntityDataPacket.create(this)

    @Suppress("DEPRECATION")
    override fun loadAdditional(input: ValueInput) {
        super.loadAdditional(input)
        loadInternalData(input.read(INTERNAL_DATA_CODEC).orElseGet(::CompoundTag))
    }

    @Suppress("DEPRECATION")
    override fun saveAdditional(output: ValueOutput) {
        super.saveAdditional(output)
        output.store(INTERNAL_DATA_CODEC, saveInternalData(CompoundTag()))
    }

    override fun pushInternalDataChangeToClient(state: BlockState?) {
        val level = getLevel() ?: return
        val realState = state ?: blockState
        if (!level.isClientSide) {
            setChanged()
            level.setBlockAndUpdate(blockPos, realState)
            level.sendBlockUpdated(blockPos, realState, realState, updateFlag)
        }
    }

    override fun triggerRenderUpdate() {
        PlatformToolkit.get().triggerRenderUpdate(this)
    }
}
