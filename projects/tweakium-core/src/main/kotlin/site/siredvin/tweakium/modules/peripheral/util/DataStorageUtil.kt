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
import net.minecraft.world.item.component.CustomData
import site.siredvin.tweakium.modules.peripheral.api.IDataStorage
import site.siredvin.tweakium.modules.peripheral.api.IPeripheralBlockEntity
import java.util.Optional
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

    override fun remove(key: String) {
        tag.remove(key)
        trigger()
    }

    override fun putCompound(key: String, tag: CompoundTag) {
        tag.put(key, tag)
        trigger()
    }

    override fun getChild(key: String): IDataStorage = CompoundTagDataStorage(tag.getCompound(key), trigger)

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
        if (customData.isEmpty) return
        val copyTag = customData.get().copyTag()
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
        if (customData.isEmpty) return
        val copyTag = customData.get().copyTag()
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
        if (customData.isEmpty) return
        val copyTag = customData.get().copyTag()
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
        if (customData.isEmpty) return
        val copyTag = customData.get().copyTag()
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
        if (customData.isEmpty) return
        val copyTag = customData.get().copyTag()
        copyTag.put(key, tag)
        setCustomData(CustomData.of(copyTag))
    }

    override fun remove(key: String) {
        val customData = extractCustomData()
        if (customData.isEmpty) return
        val copyTag = customData.get().copyTag()
        copyTag.remove(key)
        setCustomData(CustomData.of(copyTag))
    }

    override fun getChild(key: String): IDataStorage = ChildCustomDataComputerDataStorage(key, this)

    override fun mutate(func: Consumer<CompoundTag>) {
        val customData = extractCustomData()
        if (customData.isEmpty) return
        val copyTag = customData.get().copyTag()
        func.accept(copyTag)
        setCustomData(CustomData.of(copyTag))
    }
}

class ChildCustomDataComputerDataStorage(private val key: String, private val parent: CustomDataComputerDataStorage) : CustomDataComputerDataStorage() {
    override fun extractCustomData(): Optional<CustomData> {
        val innerCompound = parent.getCompound(key)
        return if (innerCompound.isEmpty) Optional.empty() else Optional.of(CustomData.of(innerCompound))
    }

    override fun setCustomData(data: CustomData) {
        parent.putCompound(key, data.copyTag())
    }
}

class PocketComputerDataStorage(private val pocket: IPocketAccess) : CustomDataComputerDataStorage() {
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
    fun getDataStorage(access: ITurtleAccess, side: TurtleSide): IDataStorage = TurtleComputerDataStorage(access, side)

    fun getDataStorage(tileEntity: IPeripheralBlockEntity): IDataStorage = CompoundTagDataStorage(tileEntity.peripheralSettings, tileEntity::markSettingsChanged)

    fun getDataStorage(pocket: IPocketAccess): IDataStorage = PocketComputerDataStorage(pocket)
}
