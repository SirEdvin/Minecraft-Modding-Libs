package site.siredvin.tweakium.modules.peripheral.util

import dan200.computercraft.api.pocket.IPocketAccess
import dan200.computercraft.api.turtle.ITurtleAccess
import dan200.computercraft.api.turtle.TurtleSide
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import site.siredvin.tweakium.modules.peripheral.api.IDataStorage
import site.siredvin.tweakium.modules.peripheral.api.IPeripheralBlockEntity
import site.siredvin.tweakium.modules.peripheral.api.IPeripheralOwner
import java.util.*
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
    override fun getList(key: String, type: Int): ListTag = tag.getList(key, type)

    override fun putList(key: String, tag: ListTag) {
        this.tag.put(key, tag)
        trigger()
    }

    override fun getUUID(key: String): UUID = this.tag.getUUID(key)

    override fun putUUID(key: String, uuid: UUID) {
        this.tag.putUUID(key, uuid)
        trigger()
    }

    override fun getBoolean(key: String): Boolean = this.tag.getBoolean(key)

    override fun putBoolean(key: String, value: Boolean) {
        this.tag.putBoolean(key, value)
        trigger()
    }

    override fun remove(key: String) {
        tag.remove(key)
        trigger()
    }

    override fun putCompound(key: String, tag: CompoundTag) {
        this.tag.put(key, tag)
        trigger()
    }

    override fun mutate(func: Consumer<CompoundTag>) {
        func.accept(tag)
        trigger()
    }
}

object DataStorageUtil {
    fun getDataStorage(compoundTag: CompoundTag): IDataStorage = CompoundTagDataStorage(
        compoundTag,
    ) { }

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

abstract class AbstractDataObject<T> {
    abstract val nbtTag: String

    abstract fun read(data: IDataStorage): T?
    abstract fun write(data: IDataStorage, value: T): Boolean

    operator fun get(storage: IDataStorage): T? {
        if (!storage.has(nbtTag)) return null
        val value = read(storage)
        if (value == null) {
            storage.remove(nbtTag)
            return null
        }
        return value
    }

    operator fun get(access: ITurtleAccess, side: TurtleSide): T? = get(DataStorageUtil.getDataStorage(access, side))

    operator fun get(access: IPocketAccess): T? = get(DataStorageUtil.getDataStorage(access))

    operator fun get(tag: CompoundTag): T? = get(DataStorageUtil.getDataStorage(tag))

    operator fun get(owner: IPeripheralOwner): T? = get(owner.dataStorage)

    operator fun set(storage: IDataStorage, value: T?) {
        if (value == null) {
            storage.remove(nbtTag)
        } else {
            val writeResult = write(storage, value)
            if (!writeResult) {
                storage.remove(nbtTag)
            }
        }
    }

    operator fun set(owner: IPeripheralOwner, blockState: T?) {
        set(owner.dataStorage, blockState)
    }

    operator fun set(access: IPocketAccess, blockState: T?) {
        set(DataStorageUtil.getDataStorage(access), blockState)
    }

    operator fun set(access: ITurtleAccess, side: TurtleSide, blockState: T?) {
        set(DataStorageUtil.getDataStorage(access, side), blockState)
    }
}

abstract class AbstractNotNullDataObject<T> {
    abstract val nbtTag: String

    abstract val default: T

    abstract fun read(data: IDataStorage): T
    abstract fun write(data: IDataStorage, value: T): Boolean

    operator fun get(storage: IDataStorage): T {
        if (!storage.has(nbtTag)) return default
        val value = read(storage)
        if (value == null) {
            storage.remove(nbtTag)
            return default
        }
        return value
    }

    operator fun get(access: ITurtleAccess, side: TurtleSide): T = get(DataStorageUtil.getDataStorage(access, side))

    operator fun get(access: IPocketAccess): T = get(DataStorageUtil.getDataStorage(access))

    operator fun get(owner: IPeripheralOwner): T = get(owner.dataStorage)

    operator fun set(storage: IDataStorage, value: T) {
        val writeResult = write(storage, value)
        if (!writeResult) {
            storage.remove(nbtTag)
        }
    }

    operator fun set(owner: IPeripheralOwner, blockState: T) {
        set(owner.dataStorage, blockState)
    }

    operator fun set(access: IPocketAccess, blockState: T) {
        set(DataStorageUtil.getDataStorage(access), blockState)
    }

    operator fun set(access: ITurtleAccess, side: TurtleSide, blockState: T) {
        set(DataStorageUtil.getDataStorage(access, side), blockState)
    }
}
