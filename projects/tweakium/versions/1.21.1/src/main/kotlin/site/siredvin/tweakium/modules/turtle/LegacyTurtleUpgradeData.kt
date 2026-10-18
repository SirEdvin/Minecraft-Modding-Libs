package site.siredvin.tweakium.modules.turtle

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag

/** Recover pre-component CC turtle data before its string upgrade ID is decoded as a default upgrade. */
object LegacyTurtleUpgradeData {
    fun restore(tag: CompoundTag) {
        for (side in listOf("Left", "Right")) {
            val upgradeKey = "${side}Upgrade"
            val dataKey = "${side}UpgradeNbt"
            // Never replace modern patches, including explicit removals, with leftover legacy data.
            if (!tag.contains(upgradeKey, Tag.TAG_STRING.toInt()) || !tag.contains(dataKey, Tag.TAG_COMPOUND.toInt())) continue
            val id = tag.getString(upgradeKey)
            val data = tag.getCompound(dataKey)
            if (id.isEmpty() || data.isEmpty) continue
            val components = CompoundTag().apply { put("minecraft:custom_data", data.copy()) }
            tag.put(upgradeKey, CompoundTag().apply {
                putString("id", id)
                put("components", components)
            })
            // Keep migration input fields intact. CC's normal save emits the restored component patch.
        }
    }
}
