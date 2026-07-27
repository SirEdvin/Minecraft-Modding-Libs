package site.siredvin.tweakium.modules.peripheral.api

import net.minecraft.nbt.CompoundTag
import site.siredvin.broccolium.modules.base.api.ITickingBlockEntity

interface IPeripheralBlockEntity : ITickingBlockEntity {
    val peripheralSettings: CompoundTag
    fun markSettingsChanged()
}
