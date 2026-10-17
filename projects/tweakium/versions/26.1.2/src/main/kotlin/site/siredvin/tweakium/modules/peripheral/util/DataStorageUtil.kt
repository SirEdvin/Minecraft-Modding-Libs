package site.siredvin.tweakium.modules.peripheral.util

import dan200.computercraft.api.pocket.IPocketAccess
import dan200.computercraft.api.turtle.ITurtleAccess
import dan200.computercraft.api.turtle.TurtleSide
import net.minecraft.core.UUIDUtil
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.component.DataComponents
import net.minecraft.core.component.TypedDataComponent
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.world.item.component.CustomData
import site.siredvin.broccolium.modules.base.util.DataComponentUtil
import site.siredvin.tweakium.modules.peripheral.api.IDataStorage
import site.siredvin.tweakium.modules.peripheral.api.IPeripheralBlockEntity
import site.siredvin.tweakium.modules.peripheral.api.IPeripheralOwner
import java.util.Optional
import java.util.UUID
import java.util.function.Consumer

private fun CompoundTag.typedList(key: String, type: Int): ListTag {
    val list = getListOrEmpty(key)
    return if (list.all { it.id.toInt() == type }) list else ListTag()
}

private fun CompoundTag.uuid(key: String): UUID = read(key, UUIDUtil.CODEC).orElseThrow { IllegalArgumentException("Invalid UUID at $key") }

class CompoundTagDataStorage(private val tag: CompoundTag, private val trigger: () -> Unit) : IDataStorage {
    companion object {
        const val PATCH_STORAGE = "__patch_storage__"
    }

    override var patch: DataComponentPatch
        get() = DataComponentUtil.nbtToPatch(tag.get(PATCH_STORAGE)) ?: DataComponentPatch.EMPTY
        set(value) {
            val nbt = DataComponentUtil.patchToNBT(value)
            tag.put(PATCH_STORAGE, checkNotNull(nbt))
            trigger()
        }

    override fun has(key: String): Boolean = tag.contains(key)

    override fun putString(key: String, value: String) {
        tag.putString(key, value)
        trigger()
    }

    override fun getString(key: String): String = tag.getStringOr(key, "")

    override fun putInt(key: String, value: Int) {
        tag.putInt(key, value)
        trigger()
    }

    override fun getInt(key: String): Int = tag.getIntOr(key, 0)
    override fun getDouble(key: String): Double = tag.getDoubleOr(key, 0.0)

    override fun putDouble(key: String, value: Double) {
        tag.putDouble(key, value)
        trigger()
    }

    override fun getCompound(key: String): CompoundTag = tag.getCompoundOrEmpty(key)
    override fun getList(key: String, type: Int): ListTag = tag.typedList(key, type)

    override fun putList(key: String, tag: ListTag) {
        this.tag.put(key, tag)
        trigger()
    }

    override fun getUUID(key: String): UUID = tag.uuid(key)

    override fun putUUID(key: String, uuid: UUID) {
        tag.store(key, UUIDUtil.CODEC, uuid)
        trigger()
    }

    override fun getBoolean(key: String): Boolean = tag.getBooleanOr(key, false)

    override fun putBoolean(key: String, value: Boolean) {
        tag.putBoolean(key, value)
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

abstract class CustomDataComputerDataStorage : IDataStorage {
    abstract fun extractCustomData(): Optional<CustomData>
    abstract fun setCustomData(data: CustomData)

    override fun has(key: String): Boolean {
        val customData = extractCustomData()
        if (customData.isEmpty) return false
        return customData.get().contains(key)
    }

    override fun putString(key: String, value: String) {
        val customData = extractCustomData()
        val copyTag = if (customData.isPresent) customData.get().copyTag() else CompoundTag()
        copyTag.putString(key, value)
        setCustomData(CustomData.of(copyTag))
    }

    override fun getString(key: String): String {
        val customData = extractCustomData()
        if (customData.isEmpty) return ""
        return customData.get().copyTag().getStringOr(key, "")
    }

    override fun putInt(key: String, value: Int) {
        val customData = extractCustomData()
        val copyTag = if (customData.isPresent) customData.get().copyTag() else CompoundTag()
        copyTag.putInt(key, value)
        setCustomData(CustomData.of(copyTag))
    }

    override fun getInt(key: String): Int {
        val customData = extractCustomData()
        if (customData.isEmpty) return 0
        return customData.get().copyTag().getIntOr(key, 0)
    }

    override fun getDouble(key: String): Double {
        val customData = extractCustomData()
        if (customData.isEmpty) return 0.0
        return customData.get().copyTag().getDoubleOr(key, 0.0)
    }

    override fun putDouble(key: String, value: Double) {
        val customData = extractCustomData()
        val copyTag = if (customData.isPresent) customData.get().copyTag() else CompoundTag()
        copyTag.putDouble(key, value)
        setCustomData(CustomData.of(copyTag))
    }

    override fun getCompound(key: String): CompoundTag {
        val customData = extractCustomData()
        if (customData.isEmpty) return CompoundTag()
        return customData.get().copyTag().getCompoundOrEmpty(key)
    }

    override fun putCompound(key: String, tag: CompoundTag) {
        val customData = extractCustomData()
        val copyTag = if (customData.isPresent) customData.get().copyTag() else CompoundTag()
        copyTag.put(key, tag)
        setCustomData(CustomData.of(copyTag))
    }

    override fun getList(key: String, type: Int): ListTag {
        val customData = extractCustomData()
        if (customData.isEmpty) return ListTag()
        return customData.get().copyTag().typedList(key, type)
    }

    override fun putList(key: String, tag: ListTag) {
        val customData = extractCustomData()
        val copyTag = if (customData.isPresent) customData.get().copyTag() else CompoundTag()
        copyTag.put(key, tag)
        setCustomData(CustomData.of(copyTag))
    }

    override fun putUUID(key: String, uuid: UUID) {
        val customData = extractCustomData()
        val copyTag = if (customData.isPresent) customData.get().copyTag() else CompoundTag()
        copyTag.store(key, UUIDUtil.CODEC, uuid)
        setCustomData(CustomData.of(copyTag))
    }

    override fun getUUID(key: String): UUID = extractCustomData().map { it.copyTag() }.orElseGet(::CompoundTag).uuid(key)

    override fun putBoolean(key: String, value: Boolean) {
        val customData = extractCustomData()
        val copyTag = if (customData.isPresent) customData.get().copyTag() else CompoundTag()
        copyTag.putBoolean(key, value)
        setCustomData(CustomData.of(copyTag))
    }

    override fun getBoolean(key: String): Boolean {
        val customData = extractCustomData()
        if (customData.isEmpty) return false
        return customData.get().copyTag().getBooleanOr(key, false)
    }

    override fun remove(key: String) {
        val customData = extractCustomData()
        if (customData.isEmpty) return
        val copyTag = customData.get().copyTag()
        copyTag.remove(key)
        setCustomData(CustomData.of(copyTag))
    }

    override fun mutate(func: Consumer<CompoundTag>) {
        val customData = extractCustomData()
        val copyTag = if (customData.isPresent) customData.get().copyTag() else CompoundTag()
        func.accept(copyTag)
        setCustomData(CustomData.of(copyTag))
    }
}

class PocketComputerDataStorage(private val pocket: IPocketAccess) : CustomDataComputerDataStorage() {
    override var patch: DataComponentPatch
        get() = pocket.upgradeData
        set(value) {
            pocket.upgradeData = value
        }

    @Suppress("UNCHECKED_CAST")
    override fun extractCustomData(): Optional<CustomData> = pocket.upgradeData.entrySet()
        .firstOrNull { it.key == DataComponents.CUSTOM_DATA }?.value as Optional<CustomData>? ?: Optional.empty()

    @Suppress("UNCHECKED_CAST")
    override fun setCustomData(data: CustomData) {
        val builder = DataComponentPatch.builder()
        pocket.upgradeData.entrySet().forEach {
            if (it.key != DataComponents.CUSTOM_DATA) {
                if (it.value.isPresent) {
                    builder.set(TypedDataComponent(it.key as DataComponentType<Any>, it.value.get()))
                } else {
                    builder.remove(it.key)
                }
            }
        }
        builder.set(DataComponents.CUSTOM_DATA, data)
        pocket.upgradeData = builder.build()
    }
}

class TurtleComputerDataStorage(private val turtle: ITurtleAccess, private val side: TurtleSide) : CustomDataComputerDataStorage() {
    override var patch: DataComponentPatch
        get() = turtle.getUpgradeData(side)
        set(value) {
            turtle.setUpgradeData(side, value)
        }

    @Suppress("UNCHECKED_CAST")
    override fun extractCustomData(): Optional<CustomData> = turtle.getUpgradeData(side).entrySet()
        .firstOrNull { it.key == DataComponents.CUSTOM_DATA }?.value as Optional<CustomData>? ?: Optional.empty()

    @Suppress("UNCHECKED_CAST")
    override fun setCustomData(data: CustomData) {
        val builder = DataComponentPatch.builder()
        turtle.getUpgradeData(side).entrySet().forEach {
            if (it.key != DataComponents.CUSTOM_DATA) {
                if (it.value.isPresent) {
                    builder.set(TypedDataComponent(it.key as DataComponentType<Any>, it.value.get()))
                } else {
                    builder.remove(it.key)
                }
            }
        }
        builder.set(DataComponents.CUSTOM_DATA, data)
        turtle.setUpgradeData(side, builder.build())
    }
}

object DataStorageUtil {
    fun getDataStorage(compoundTag: CompoundTag): IDataStorage = CompoundTagDataStorage(compoundTag) { }
    fun getDataStorage(access: ITurtleAccess, side: TurtleSide): IDataStorage = TurtleComputerDataStorage(access, side)
    fun getDataStorage(tileEntity: IPeripheralBlockEntity): IDataStorage = CompoundTagDataStorage(tileEntity.peripheralSettings, tileEntity::markSettingsChanged)
    fun getDataStorage(pocket: IPocketAccess): IDataStorage = PocketComputerDataStorage(pocket)
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
            if (!write(storage, value)) storage.remove(nbtTag)
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
        if (!write(storage, value)) storage.remove(nbtTag)
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
