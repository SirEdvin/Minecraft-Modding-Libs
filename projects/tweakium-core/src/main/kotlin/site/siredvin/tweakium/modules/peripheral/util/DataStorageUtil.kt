package site.siredvin.tweakium.modules.peripheral.util

import dan200.computercraft.api.pocket.IPocketAccess
import dan200.computercraft.api.turtle.ITurtleAccess
import dan200.computercraft.api.turtle.TurtleSide
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.component.DataComponents
import net.minecraft.core.component.TypedDataComponent
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.NbtUtils
import net.minecraft.world.item.component.CustomData
import site.siredvin.broccolium.modules.base.util.DataComponentUtil
import site.siredvin.tweakium.modules.peripheral.api.IDataStorage
import site.siredvin.tweakium.modules.peripheral.api.IPeripheralBlockEntity
import java.util.*
import java.util.Optional
import java.util.function.Consumer

class CompoundTagDataStorage(private val tag: CompoundTag, private val trigger: () -> Unit) : IDataStorage {
    companion object {
        const val PATCH_STORAGE = "__patch_storage__"
    }

    override var patch: DataComponentPatch
        get() = DataComponentUtil.nbtToPatch(tag.get(PATCH_STORAGE)) ?: DataComponentPatch.EMPTY
        set(value) {
            val nbt = DataComponentUtil.patchToNBT(value)
            if (nbt != null) {
                tag.put(PATCH_STORAGE, nbt)
            }
        }

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
        tag.put(key, tag)
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
        if (customData.isEmpty) {
            return false
        }
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
        return customData.get().copyTag().getString(key)
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
        return customData.get().copyTag().getInt(key)
    }

    override fun getDouble(key: String): Double {
        val customData = extractCustomData()
        if (customData.isEmpty) return 0.0
        return customData.get().copyTag().getDouble(key)
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
        return customData.get().copyTag().getCompound(key)
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
        return customData.get().copyTag().getList(key, type)
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
        copyTag.putUUID(key, uuid)
        setCustomData(CustomData.of(copyTag))
    }

    override fun getUUID(key: String): UUID {
        val customData = extractCustomData()
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        if (customData.isEmpty) return NbtUtils.loadUUID(null)
        return customData.get().copyTag().getUUID(key)
    }

    override fun putBoolean(key: String, value: Boolean) {
        val customData = extractCustomData()
        val copyTag = if (customData.isPresent) customData.get().copyTag() else CompoundTag()
        copyTag.putBoolean(key, value)
        setCustomData(CustomData.of(copyTag))
    }

    override fun getBoolean(key: String): Boolean {
        val customData = extractCustomData()
        if (customData.isEmpty) return false
        return customData.get().copyTag().getBoolean(key)
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
    override fun extractCustomData(): Optional<CustomData> {
        val data = pocket.upgradeData.get(DataComponents.CUSTOM_DATA) ?: return Optional.empty()
        return data as Optional<CustomData>
    }

    @Suppress("UNCHECKED_CAST")
    override fun setCustomData(data: CustomData) {
        val builder = DataComponentPatch.builder()
        pocket.upgradeData.entrySet().forEach {
            if (it.key != DataComponents.CUSTOM_DATA && it.value.isPresent) {
                builder.set(TypedDataComponent(it.key as DataComponentType<Any>, it.value.get()))
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
    override fun extractCustomData(): Optional<CustomData> {
        val data = turtle.getUpgradeData(side).get(DataComponents.CUSTOM_DATA) ?: return Optional.empty()
        return data as Optional<CustomData>
    }

    @Suppress("UNCHECKED_CAST")
    override fun setCustomData(data: CustomData) {
        val builder = DataComponentPatch.builder()
        turtle.getUpgradeData(side).entrySet().forEach {
            if (it.key != DataComponents.CUSTOM_DATA && it.value.isPresent) {
                builder.set(TypedDataComponent(it.key as DataComponentType<Any>, it.value.get()))
            }
        }
        builder.set(DataComponents.CUSTOM_DATA, data)
        turtle.setUpgradeData(side, builder.build())
    }
}

object DataStorageUtil {
    fun getDataStorage(compoundTag: CompoundTag): IDataStorage = CompoundTagDataStorage(
        compoundTag,
    ) { }

    fun getDataStorage(access: ITurtleAccess, side: TurtleSide): IDataStorage = TurtleComputerDataStorage(access, side)

    fun getDataStorage(tileEntity: IPeripheralBlockEntity): IDataStorage = CompoundTagDataStorage(tileEntity.peripheralSettings, tileEntity::markSettingsChanged)

    fun getDataStorage(pocket: IPocketAccess): IDataStorage = PocketComputerDataStorage(pocket)
}
