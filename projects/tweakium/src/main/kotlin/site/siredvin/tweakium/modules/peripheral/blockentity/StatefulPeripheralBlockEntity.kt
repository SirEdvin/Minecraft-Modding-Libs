package site.siredvin.tweakium.modules.peripheral.blockentity

import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import site.siredvin.broccolium.modules.base.api.ISavableBlockEntity
import site.siredvin.tweakium.modules.peripheral.api.IOwnedPeripheral

abstract class StatefulPeripheralBlockEntity<T : IOwnedPeripheral<*>>(
    blockEntityType: BlockEntityType<*>,
    blockPos: BlockPos,
    blockState: BlockState,
) : PeripheralBlockEntity<T>(blockEntityType, blockPos, blockState),
    ISavableBlockEntity {
    override fun saveSavableData(data: CompoundTag): CompoundTag {
        if (!peripheralSettings.isEmpty) {
            peripheralSettings.allKeys.forEach {
                data.put(it, peripheralSettings.get(it)!!)
            }
        }
        return data
    }

    override fun loadSavableData(
        data: CompoundTag,
        state: BlockState?,
    ): BlockState {
        if (!data.isEmpty) {
            data.allKeys.forEach {
                peripheralSettings.put(it, data.get(it)!!)
            }
        }
        return state ?: blockState
    }
}
