package site.siredvin.tweakium.modules.peripheral.api

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import java.util.UUID
import java.util.function.Consumer

interface IDataStorage {
    fun has(key: String): Boolean
    fun putString(key: String, value: String)
    fun getString(key: String): String
    fun putInt(key: String, value: Int)
    fun getInt(key: String): Int
    fun getDouble(key: String): Double
    fun putDouble(key: String, value: Double)
    fun putCompound(key: String, tag: CompoundTag)
    fun getCompound(key: String): CompoundTag
    fun getList(key: String, type: Int): ListTag
    fun putList(key: String, tag: ListTag)
    fun getUUID(key: String): UUID
    fun putUUID(key: String, uuid: UUID)
    fun getBoolean(key: String): Boolean
    fun putBoolean(key: String, value: Boolean)

    fun remove(key: String)
    fun mutate(func: Consumer<CompoundTag>)
}
