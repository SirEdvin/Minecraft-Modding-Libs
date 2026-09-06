package site.siredvin.tweakium

import net.minecraft.SharedConstants
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.Bootstrap
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import site.siredvin.tweakium.modules.peripheral.util.CompoundTagDataStorage

class DataStorageTests {
    companion object {
        @JvmStatic
        @BeforeAll
        fun bootstrap() {
            SharedConstants.tryDetectVersion()
            Bootstrap.bootStrap()
        }
    }

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
        val invalid = DataComponentPatch.builder().set(DataComponents.MAX_STACK_SIZE, 0).build()
        assertThrows<IllegalStateException> { storage.patch = invalid }
        assertEquals(saved, tag)
        assertEquals(1, changes)
    }
}
