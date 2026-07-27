package site.siredvin.broccolium.modules.base.api

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.state.BlockState

interface ISyncingBlockEntity : ISavableBlockEntity {
    fun saveInternalData(data: CompoundTag): CompoundTag
    fun loadInternalData(data: CompoundTag, state: BlockState? = null): BlockState
    fun pushInternalDataChangeToClient(state: BlockState? = null)
    fun triggerRenderUpdate()
    override fun saveSavableData(data: CompoundTag): CompoundTag = this.saveInternalData(data)

    override fun loadSavableData(data: CompoundTag, state: BlockState?): BlockState = this.loadInternalData(data, state)
}
