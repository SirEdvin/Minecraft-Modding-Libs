package site.siredvin.tweakium.modules.peripheral.util

import dan200.computercraft.api.pocket.IPocketAccess
import dan200.computercraft.api.turtle.ITurtleAccess
import dan200.computercraft.api.turtle.TurtleSide
import net.minecraft.nbt.CompoundTag
import site.siredvin.tweakium.modules.peripheral.api.IPeripheralBlockEntity

object DataStorageUtil {
    fun getDataStorage(access: ITurtleAccess, side: TurtleSide?): CompoundTag = access.getUpgradeNBTData(side)

    fun getDataStorage(tileEntity: IPeripheralBlockEntity): CompoundTag = tileEntity.peripheralSettings

    fun getDataStorage(pocket: IPocketAccess): CompoundTag = pocket.upgradeNBTData
}
