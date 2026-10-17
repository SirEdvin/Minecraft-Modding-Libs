package site.siredvin.broccolium.test

import com.mojang.serialization.JsonOps
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.world.SimpleContainer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.material.Fluids
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import site.siredvin.broccolium.modules.platform.FabricRegistryWrapper
import site.siredvin.broccolium.modules.storage.FabricStorageUtils
import site.siredvin.broccolium.modules.storage.fluid.toVanilla
import site.siredvin.broccolium.modules.storage.fluid.toVariant
import site.siredvin.broccolium.modules.storage.item.ContainerWrapper

@WithMinecraft
@ExtendWith(BroccoliumInitialization::class)
class FabricTargetAdapterTests {
    @Test
    fun fluidConversionRetainsValuesRemovalsAndNativeUnits() {
        val patch = DataComponentPatch.builder()
            .set(DataComponents.CUSTOM_NAME, Component.literal("fixture fluid"))
            .remove(DataComponents.ENCHANTMENTS)
            .build()
        val variant = FluidVariant.of(Fluids.WATER, patch)
        val stack = variant.toVanilla(81000.0)
        assertEquals(1000.0, stack.amount)
        assertEquals(patch, stack.components)
        assertEquals(variant, stack.toVariant())
        assertTrue(stack.components.entrySet().any { it.key == DataComponents.ENCHANTMENTS && it.value.isEmpty })
    }

    @Test
    fun registryMembershipRejectsUnknownValuesInsteadOfReturningDefault() {
        val wrapper = FabricRegistryWrapper(Identifier.withDefaultNamespace("item"), BuiltInRegistries.ITEM)
        val missing = Identifier.fromNamespaceAndPath("broccolium", "missing_test_item")
        assertNull(wrapper.tryGet(missing))
        assertThrows(IllegalArgumentException::class.java) { wrapper.get(missing) }
        assertThrows(IllegalArgumentException::class.java) { wrapper.get(-1) }
        assertSame(Items.STONE, wrapper.get(wrapper.getId(Items.STONE)))
        assertEquals(Identifier.withDefaultNamespace("stone"), wrapper.getResourceKey(Items.STONE).orElseThrow().identifier())
        val encoded = wrapper.byNameCodec().encodeStart(JsonOps.INSTANCE, Items.STONE).getOrThrow()
        assertSame(Items.STONE, wrapper.byNameCodec().parse(JsonOps.INSTANCE, encoded).getOrThrow())
    }

    @Test
    fun abortedNestedSlotMutationRestoresExactComponentsAndCount() {
        val original = ItemStack(Items.STONE, 9).also { it.set(DataComponents.CUSTOM_NAME, Component.literal("original")) }
        val container = SimpleContainer(1).also { it.setItem(0, original.copy()) }
        val slot = FabricStorageUtils.getSlot(ContainerWrapper(container), 0)
        Transaction.openOuter().use { outer ->
            outer.openNested().use { nested ->
                assertEquals(4L, slot.extract(ItemVariant.of(original), 4, nested))
                nested.commit()
            }
            assertEquals(5, container.getItem(0).count)
        }
        assertTrue(ItemStack.matches(original, container.getItem(0)))
        assertEquals(9L, slot.amount)
    }

    @Test
    fun committedSlotMutationPreservesDetachedCallerStack() {
        val original = ItemStack(Items.STONE, 9)
        val container = SimpleContainer(1).also { it.setItem(0, original.copy()) }
        val slot = FabricStorageUtils.getSlot(ContainerWrapper(container), 0)
        Transaction.openOuter().use { transaction ->
            assertEquals(4L, slot.extract(ItemVariant.of(original), 4, transaction))
            transaction.commit()
        }
        assertEquals(5, container.getItem(0).count)
        assertEquals(9, original.count)
    }
}
