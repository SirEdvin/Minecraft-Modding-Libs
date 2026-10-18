package site.siredvin.tweakium

import net.minecraft.nbt.CompoundTag
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import site.siredvin.tweakium.modules.turtle.LegacyTurtleUpgradeData

class LegacyTurtleUpgradeDataTests {
    @Test
    fun bothLegacySidesKeepIndependentCustomDataAndOriginalInputFields() {
        val tag = CompoundTag()
        for (side in listOf("Left", "Right")) {
            tag.putString("${side}Upgrade", "computercraft:speaker")
            tag.put("${side}UpgradeNbt", CompoundTag().apply { putString("ownerProfile", side) })
        }
        LegacyTurtleUpgradeData.restore(tag)
        for (side in listOf("Left", "Right")) {
            val restored = tag.getCompound("${side}Upgrade")
            assertEquals("computercraft:speaker", restored.getString("id"))
            val custom = restored.getCompound("components").getCompound("minecraft:custom_data")
            assertEquals(side, custom.getString("ownerProfile"))
            custom.putString("ownerProfile", "changed")
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
            put("LeftUpgradeNbt", CompoundTag().apply { putString("ownerProfile", "stale") })
        }
        val original = tag.copy()
        LegacyTurtleUpgradeData.restore(tag)
        assertEquals(original, tag)
        assertTrue(tag.getCompound("LeftUpgrade").getCompound("components").contains("!minecraft:custom_data"))
    }

    @Test
    fun missingEmptyOrMalformedDataDoesNotCreateUpgradeOrMutateTag() {
        for (value in listOf(CompoundTag(), CompoundTag().apply { putString("LeftUpgradeNbt", "invalid") }, CompoundTag().apply { put("LeftUpgradeNbt", CompoundTag()) })) {
            value.putString("LeftUpgrade", "computercraft:speaker")
            val original = value.copy()
            LegacyTurtleUpgradeData.restore(value)
            assertEquals(original, value)
            assertFalse(value.contains("RightUpgrade"))
        }
        val removed = CompoundTag().apply { put("LeftUpgradeNbt", CompoundTag().apply { putInt("setting", 37) }) }
        val original = removed.copy()
        LegacyTurtleUpgradeData.restore(removed)
        assertEquals(original, removed)
    }

    @Test
    fun restorationIsIdempotent() {
        val tag = CompoundTag().apply {
            putString("LeftUpgrade", "computercraft:speaker")
            put("LeftUpgradeNbt", CompoundTag().apply { putInt("setting", 37) })
        }
        LegacyTurtleUpgradeData.restore(tag)
        val original = tag.copy()
        LegacyTurtleUpgradeData.restore(tag)
        assertEquals(original, tag)
    }
}
