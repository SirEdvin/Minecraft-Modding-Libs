package site.siredvin.broccolium.test

import com.mojang.serialization.Codec
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.component.DataComponentType
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.material.Fluids
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import site.siredvin.broccolium.modules.storage.fluid.AgnosticFluidStack

@WithMinecraft
@ExtendWith(BroccoliumInitialization::class)
class FluidPersistenceTests {
    @Test
    fun encodeFailureCannotPartiallyMutateCallerNbt() {
        val unregistered = DataComponentType.builder<String>().persistent(Codec.STRING).build()
        val invalid = AgnosticFluidStack(Fluids.LAVA, 19.0, DataComponentPatch.builder().set(unregistered, "invalid").build())
        val target = CompoundTag().apply { putString("sentinel", "retained") }
        val before = target.copy()
        assertThrows(RuntimeException::class.java) { invalid.save(target) }
        assertEquals(before, target)
    }
}
