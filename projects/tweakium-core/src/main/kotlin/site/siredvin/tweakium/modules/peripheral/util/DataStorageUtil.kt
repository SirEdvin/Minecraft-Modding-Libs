package site.siredvin.tweakium.modules.peripheral.util

import dan200.computercraft.api.pocket.IPocketAccess
import dan200.computercraft.api.turtle.ITurtleAccess
import dan200.computercraft.api.turtle.TurtleSide
import net.minecraft.nbt.CompoundTag
import site.siredvin.tweakium.modules.peripheral.api.IDataStorage
import site.siredvin.tweakium.modules.peripheral.api.IPeripheralBlockEntity
import java.util.function.Consumer

class CompoundTagDataStorage(private val tag: CompoundTag, private val trigger: () -> Unit) : IDataStorage {
    override fun has(key: String): Boolean = tag.contains(key)

    override fun putString(key: String, value: String) {
        tag.putString(key, value)
        trigger()
    }

    override fun getString(key: String): String = tag.getString(key)

    override fun putInt(key: String, value: Int) {
        tag.putInt(key, value)
        trigger()
    }

    override fun getInt(key: String): Int = tag.getInt(key)

    override fun getDouble(key: String): Double = tag.getDouble(key)

    override fun putDouble(key: String, value: Double) {
        tag.putDouble(key, value)
        trigger()
    }

    override fun getCompound(key: String): CompoundTag = tag.getCompound(key)

    override fun putCompound(key: String, tag: CompoundTag) {
        tag.put(key, tag)
        trigger()
    }

    override fun mutate(func: Consumer<CompoundTag>) {
        func.accept(tag)
        trigger()
    }
}

object DataStorageUtil {
    fun getDataStorage(access: ITurtleAccess, side: TurtleSide?): IDataStorage = CompoundTagDataStorage(
        access.getUpgradeNBTData(side),
    ) { access.updateUpgradeNBTData(side) }

    fun getDataStorage(tileEntity: IPeripheralBlockEntity): IDataStorage = CompoundTagDataStorage(
        tileEntity.peripheralSettings,
        tileEntity::markSettingsChanged,
    )

    fun getDataStorage(pocket: IPocketAccess): IDataStorage = CompoundTagDataStorage(
        pocket.upgradeNBTData,
        pocket::updateUpgradeNBTData,
    )
}
