package site.siredvin.tweakium.modules.peripheral.owner

import net.minecraft.core.Direction
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.properties.EnumProperty
import site.siredvin.broccolium.modules.base.block.FacingBlockEntityBlock
import site.siredvin.tweakium.modules.peripheral.api.IDataStorage
import site.siredvin.tweakium.modules.peripheral.api.IPeripheralBlockEntity
import site.siredvin.tweakium.modules.peripheral.util.DataStorageUtil

class BlockEntityPeripheralOwner<T>(blockEntity: T, facingProperty: EnumProperty<Direction> = FacingBlockEntityBlock.FACING) : RawBlockEntityPeripheralOwner<T>(blockEntity, facingProperty) where T : BlockEntity, T : IPeripheralBlockEntity {
    override val dataStorage: IDataStorage
        get() = DataStorageUtil.getDataStorage(blockEntity)
}
