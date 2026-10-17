package site.siredvin.tweakium.modules.peripheral.util

import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.minecraft.core.BlockPos
import net.minecraft.nbt.*
import net.minecraft.world.level.ChunkPos
import java.io.ByteArrayInputStream
import java.io.IOException
import java.util.*

object NBTUtil {
    fun toDirectNBT(`object`: Any?): Tag? = when (`object`) {
        null -> null
        is Boolean -> ByteTag.valueOf((if (`object`) 1 else 0).toByte())
        is Int -> IntTag.valueOf(`object`)
        is Number -> DoubleTag.valueOf(`object`.toDouble())
        is String -> StringTag.valueOf(`object`)
        is Map<*, *> -> {
            val nbt = CompoundTag()
            for ((key, raw) in `object`) {
                val value = toDirectNBT(raw)
                if (key != null && value != null) nbt.put(key.toString(), value)
            }
            nbt
        }
        else -> null
    }

    fun fromText(json: String?): CompoundTag? = try {
        if (json == null) null else TagParser.parseCompoundFully(json)
    } catch (ex: CommandSyntaxException) {
        ex.printStackTrace()
        null
    }

    fun fromBinary(base64: String?): CompoundTag? {
        if (base64 == null) return null
        try {
            Base64.getDecoder().wrap(ByteArrayInputStream(base64.toByteArray()))
                .use { inputStream -> return NbtIo.readCompressed(inputStream, NbtAccounter(1024, 10240)) }
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
    }

    fun toNBT(pos: BlockPos): CompoundTag = CompoundTag().apply {
        putInt("x", pos.x)
        putInt("y", pos.y)
        putInt("z", pos.z)
    }

    fun blockPosFromNBT(nbt: CompoundTag): BlockPos = BlockPos(nbt.getIntOr("x", 0), nbt.getIntOr("y", 0), nbt.getIntOr("z", 0))

    fun toNBT(pos: ChunkPos): CompoundTag = CompoundTag().apply {
        putInt("x", pos.x)
        putInt("z", pos.z)
    }

    fun chunkPosFromNBT(nbt: CompoundTag): ChunkPos = ChunkPos(nbt.getIntOr("x", 0), nbt.getIntOr("z", 0))

    fun isSubSet(set: CompoundTag, subset: CompoundTag): Boolean {
        for (key in subset.keySet()) {
            if (!set.contains(key)) return false
            val original = subset.get(key)
            val setData = set.get(key)
            if (original is CompoundTag) {
                if (setData !is CompoundTag) return false
                if (!isSubSet(setData, original)) return false
            }
            if (original != null && original != setData) return false
        }
        return true
    }
}
