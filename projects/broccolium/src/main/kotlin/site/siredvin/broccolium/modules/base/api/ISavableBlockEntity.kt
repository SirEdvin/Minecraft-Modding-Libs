package site.siredvin.broccolium.modules.base.api

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.state.BlockState

interface ISavableBlockEntity {
    fun saveSavableData(data: CompoundTag): CompoundTag
    fun loadSavableData(data: CompoundTag, state: BlockState? = null): BlockState
}
