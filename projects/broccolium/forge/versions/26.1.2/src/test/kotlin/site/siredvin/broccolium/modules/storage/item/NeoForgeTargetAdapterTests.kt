package site.siredvin.broccolium.modules.storage.item

import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.world.SimpleContainer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.material.Fluids
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler
import net.neoforged.neoforge.transfer.fluid.FluidResource
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler
import net.neoforged.neoforge.transfer.item.ItemResource
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler
import net.neoforged.neoforge.transfer.transaction.Transaction
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import site.siredvin.broccolium.modules.storage.energy.AgnosticEnergyHandlerWrapper
import site.siredvin.broccolium.modules.storage.energy.AgnosticEnergyStack
import site.siredvin.broccolium.modules.storage.energy.Energies
import site.siredvin.broccolium.modules.storage.fluid.AgnosticFluidStack
import site.siredvin.broccolium.modules.storage.fluid.ForgeAgnosticFluidStorage
import site.siredvin.broccolium.test.WithNeoForgeRegistries
import site.siredvin.broccolium.modules.storage.base.api.SlottedAgnosticStorage
import java.util.function.Predicate

@WithNeoForgeRegistries
class NeoForgeTargetAdapterTests {
    @Test
    fun itemSimulationAndExtractionDoNotLoseMixedVariantsOrOverextract() {
        val native = ItemStacksResourceHandler(3)
        Transaction.openRoot().use { transaction ->
            native.insert(0, ItemResource.of(Items.STONE), 40, transaction)
            native.insert(1, ItemResource.of(Items.DIRT), 30, transaction)
            native.insert(2, ItemResource.of(Items.STONE), 40, transaction)
            transaction.commit()
        }
        val storage = AgnosticItemHandlerWrapper(native)
        assertEquals(64, storage.take(Predicate { true }, 128, true).count)
        assertEquals(40L, native.getAmountAsLong(0))
        assertEquals(40L, native.getAmountAsLong(2))
        val extracted = storage.take(Predicate { true }, 128, false)
        assertSame(Items.STONE, extracted.item)
        assertEquals(64, extracted.count)
        assertEquals(0L, native.getAmountAsLong(0))
        assertEquals(30L, native.getAmountAsLong(1))
        assertEquals(16L, native.getAmountAsLong(2))
        assertThrows(IllegalArgumentException::class.java) { storage.take(Predicate { true }, -1, false) }
    }

    @Test
    fun energyLimitsClampBeforeNativeIntConversionAndSimulationAborts() {
        val native = SimpleEnergyHandler(Int.MAX_VALUE)
        val storage = AgnosticEnergyHandlerWrapper(native)
        val offered = AgnosticEnergyStack(Energies.FORGE, Long.MAX_VALUE)
        assertEquals(Long.MAX_VALUE - Int.MAX_VALUE, storage.store(offered, true).amount)
        assertEquals(0L, native.amountAsLong)
        assertEquals(Long.MAX_VALUE - Int.MAX_VALUE, storage.store(offered, false).amount)
        assertEquals(Int.MAX_VALUE.toLong(), native.amountAsLong)
        assertEquals(Int.MAX_VALUE.toLong(), storage.take(Predicate { true }, Long.MAX_VALUE, true).amount)
        assertEquals(Int.MAX_VALUE.toLong(), native.amountAsLong)
        assertEquals(Int.MAX_VALUE.toLong(), storage.take(Predicate { true }, Long.MAX_VALUE, false).amount)
        assertEquals(0L, native.amountAsLong)
    }

    @Test
    fun fluidSimulationAndIntBoundPreserveTheFullRemainder() {
        val native = FluidStacksResourceHandler(1, Int.MAX_VALUE)
        val storage = ForgeAgnosticFluidStorage(native)
        val offered = AgnosticFluidStack(Fluids.WATER, Int.MAX_VALUE.toDouble() + 1000)
        assertEquals(1000.0, storage.store(offered, true).amount)
        assertEquals(0L, native.getAmountAsLong(0))
        assertEquals(1000.0, storage.store(offered, false).amount)
        assertEquals(Int.MAX_VALUE.toLong(), native.getAmountAsLong(0))
        assertThrows(IllegalArgumentException::class.java) { storage.take(Predicate { true }, Double.NaN, false) }
        assertThrows(IllegalArgumentException::class.java) { storage.take(Predicate { true }, -1.0, false) }
    }

    @Test
    fun nestedAccessExchangeAbortRestoresExactStackAndComponents() {
        val original = ItemStack(Items.BUCKET).also { it.set(DataComponents.CUSTOM_NAME, Component.literal("original")) }
        val container = SimpleContainer(1).also { it.setItem(0, original.copy()) }
        val origin = ContainerWrapper(container)
        val access = AgnosticItemAccess.of(origin, 0)
        assertSame(access, AgnosticItemAccess.of(origin, 0))
        Transaction.openRoot().use { outer ->
            Transaction.open(outer).use { nested ->
                assertEquals(1, access.exchange(ItemResource.of(Items.WATER_BUCKET), 1, nested))
                nested.commit()
            }
            assertSame(Items.WATER_BUCKET, container.getItem(0).item)
        }
        assertTrue(ItemStack.matches(original, container.getItem(0)))
    }

    @Test
    fun realBucketCapabilityReplacesOriginAndRollsBackThenCommits() {
        val container = SimpleContainer(1).also { it.setItem(0, ItemStack(Items.BUCKET)) }
        val origin = ContainerWrapper(container)
        val access = AgnosticItemAccess.of(origin, 0)
        val handler = requireNotNull(access.getCapability(Capabilities.Fluid.ITEM))
        Transaction.openRoot().use { transaction ->
            assertEquals(1000, handler.insert(FluidResource.of(Fluids.WATER), 1000, transaction))
            assertSame(Items.WATER_BUCKET, container.getItem(0).item)
        }
        assertSame(Items.BUCKET, container.getItem(0).item)
        Transaction.openRoot().use { transaction ->
            assertEquals(1000, handler.insert(FluidResource.of(Fluids.WATER), 1000, transaction))
            transaction.commit()
        }
        assertSame(Items.WATER_BUCKET, container.getItem(0).item)
    }

    @Test
    fun unsupportedReverseBackingFailsBeforeMutation() {
        val container = SimpleContainer(1).also { it.setItem(0, ItemStack(Items.BUCKET)) }
        val backing = ContainerWrapper(container)
        val origin = object : SlottedAgnosticStorage<ItemStack, Int> by backing {}
        assertThrows(UnsupportedOperationException::class.java) { AgnosticItemAccess.of(origin, 0) }
        assertSame(Items.BUCKET, origin.get(0).item)
        assertEquals(1, origin.get(0).count)
    }
}
