package site.siredvin.tweakium.modules.peripheral.owner

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.properties.DirectionProperty
import site.siredvin.broccolium.modules.base.block.FacingBlockEntityBlock
import site.siredvin.tweakium.modules.peripheral.api.IPeripheralBlockEntity
import site.siredvin.tweakium.modules.peripheral.util.DataStorageUtil

class BlockEntityPeripheralOwner<T>(blockEntity: T, facingProperty: DirectionProperty = FacingBlockEntityBlock.FACING) : RawBlockEntityPeripheralOwner<T>(blockEntity, facingProperty) where T : BlockEntity, T : IPeripheralBlockEntity {
    override val dataStorage: CompoundTag
        get() = DataStorageUtil.getDataStorage(blockEntity)

    override fun markDataStorageDirty() {
        blockEntity.setChanged()
    }
}
