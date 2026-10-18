package site.siredvin.tweakium

import net.minecraft.core.UUIDUtil
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.IntArrayTag
import net.minecraft.world.item.component.CustomData
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import site.siredvin.broccolium.test.WithMinecraft
import site.siredvin.tweakium.modules.peripheral.util.CompoundTagDataStorage
import site.siredvin.tweakium.modules.peripheral.util.CustomDataComputerDataStorage
import java.util.Optional
import java.util.UUID

@WithMinecraft
class DataStorageTests {
    @Test
    fun patchUpdatesAndClearsNotifyPersistence() {
        var changes = 0
        val storage = CompoundTagDataStorage(CompoundTag()) { changes++ }
        val patch = DataComponentPatch.builder().set(DataComponents.DAMAGE, 5).build()
        storage.patch = patch
        assertEquals(patch, storage.patch)
        assertEquals(1, changes)
        storage.patch = DataComponentPatch.EMPTY
        assertTrue(storage.patch.isEmpty)
        assertEquals(2, changes)
    }

    @Test
    fun failedEncodingPreservesStoredPatchWithoutNotification() {
        val tag = CompoundTag()
        var changes = 0
        val storage = CompoundTagDataStorage(tag) { changes++ }
        storage.patch = DataComponentPatch.builder().set(DataComponents.DAMAGE, 5).build()
        val saved = tag.copy()
        assertThrows<IllegalStateException> { storage.patch = DataComponentPatch.builder().set(DataComponents.MAX_STACK_SIZE, 0).build() }
        assertEquals(saved, tag)
        assertEquals(1, changes)
    }

    @Test
    fun customAndCompoundUuidReadsPreserveMissingMalformedAndValidContracts() {
        val tag = CompoundTag()
        var writes = 0
        val custom = object : CustomDataComputerDataStorage() {
            override var patch = DataComponentPatch.EMPTY
            override fun extractCustomData(): Optional<CustomData> = Optional.of(CustomData.of(tag.copy()))
            override fun setCustomData(data: CustomData) {
                writes++
            }
        }
        val compound = CompoundTagDataStorage(tag) { writes++ }
        for (storage in listOf(custom, compound)) assertThrows<IllegalArgumentException> { storage.getUUID("owner") }
        tag.putString("owner", "not-a-uuid")
        for (storage in listOf(custom, compound)) assertThrows<IllegalArgumentException> { storage.getUUID("owner") }
        tag.put("owner", IntArrayTag(intArrayOf(1, 2, 3)))
        for (storage in listOf(custom, compound)) assertThrows<IllegalArgumentException> { storage.getUUID("owner") }
        val owner = UUID.fromString("1da2d610-d080-4f35-9650-7c4c68d595c1")
        tag.store("owner", UUIDUtil.CODEC, owner)
        for (storage in listOf(custom, compound)) assertEquals(owner, storage.getUUID("owner"))
        assertEquals(0, writes)
        assertEquals("", custom.getString("missing"))
        assertEquals(0, custom.getInt("missing"))
        assertEquals(false, custom.getBoolean("missing"))
    }
}
