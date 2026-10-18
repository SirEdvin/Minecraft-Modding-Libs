package site.siredvin.tweakium

import com.mojang.serialization.Dynamic
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import site.siredvin.tweakium.modules.turtle.LegacyTurtleUpgradeData

class LegacyTurtleUpgradeDataTests {
    private fun restore(tag: CompoundTag): CompoundTag = LegacyTurtleUpgradeData.restore(Dynamic(NbtOps.INSTANCE, tag)).value as CompoundTag

    @Test
    fun bothLegacySidesKeepIndependentCustomDataAndOriginalInputFields() {
        val tag = CompoundTag().apply { putString("id", "computercraft:turtle_normal") }
        for (side in listOf("Left", "Right")) {
            tag.putString("${side}Upgrade", "computercraft:speaker")
            tag.put("${side}UpgradeNbt", CompoundTag().apply { putString("ownerProfile", side) })
        }
        val original = tag.copy()
        val migrated = restore(tag)
        assertEquals(original, tag)
        for (side in listOf("Left", "Right")) {
            val restored = migrated.getCompound("${side}Upgrade")
            assertEquals("computercraft:speaker", restored.getString("id"))
            val custom = restored.getCompound("components").getCompound("minecraft:custom_data")
            assertEquals(side, custom.getString("ownerProfile"))
            assertEquals(side, tag.getCompound("${side}UpgradeNbt").getString("ownerProfile"))
        }
    }

    @Test
    fun modernPatchesAndExplicitRemovalsWinOverStaleLegacyData() {
        val modern = CompoundTag().apply {
            putString("id", "computercraft:speaker")
            put("components", CompoundTag().apply { put("!minecraft:custom_data", CompoundTag()) })
        }
        val tag = CompoundTag().apply {
            put("LeftUpgrade", modern.copy())
            putString("id", "computercraft:turtle_advanced")
            put("LeftUpgradeNbt", CompoundTag().apply { putString("ownerProfile", "stale") })
        }
        val original = tag.copy()
        assertEquals(original, restore(tag))
        assertEquals(original, tag)
        assertTrue(tag.getCompound("LeftUpgrade").getCompound("components").contains("!minecraft:custom_data"))
    }

    @Test
    fun missingEmptyOrMalformedDataDoesNotCreateUpgradeOrMutateTag() {
        for (value in listOf(CompoundTag(), CompoundTag().apply { putString("LeftUpgradeNbt", "invalid") }, CompoundTag().apply { put("LeftUpgradeNbt", CompoundTag()) })) {
            value.putString("LeftUpgrade", "computercraft:speaker")
            value.putString("id", "computercraft:turtle_normal")
            val original = value.copy()
            assertEquals(original, restore(value))
            assertEquals(original, value)
            assertFalse(value.contains("RightUpgrade"))
        }
        val removed = CompoundTag().apply {
            putString("id", "computercraft:turtle_normal")
            put("LeftUpgradeNbt", CompoundTag().apply { putInt("setting", 37) })
        }
        val original = removed.copy()
        assertEquals(original, restore(removed))
        assertEquals(original, removed)
    }

    @Test
    fun restorationIsIdempotent() {
        val tag = CompoundTag().apply {
            putString("id", "computercraft:turtle_normal")
            putString("LeftUpgrade", "computercraft:speaker")
            put("LeftUpgradeNbt", CompoundTag().apply { putInt("setting", 37) })
        }
        val migrated = restore(tag)
        assertEquals(migrated, restore(migrated))
    }

    @Test
    fun unrelatedBlockEntityIsUnchanged() {
        val tag = CompoundTag().apply {
            putString("id", "fixture:unrelated")
            putString("LeftUpgrade", "computercraft:speaker")
            put("LeftUpgradeNbt", CompoundTag().apply { putInt("setting", 37) })
        }
        assertEquals(tag, restore(tag))
    }
}
