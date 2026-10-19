package site.siredvin.broccolium.testmod

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.component.DataComponents
import net.minecraft.gametest.framework.GameTestHelper
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.material.Fluids
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler
import net.neoforged.neoforge.transfer.fluid.FluidResource
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler
import net.neoforged.neoforge.transfer.transaction.Transaction
import site.siredvin.broccolium.modules.storage.ForgeStorageUtils
import site.siredvin.broccolium.modules.storage.base.api.SlottedAgnosticStorage
import site.siredvin.broccolium.modules.storage.energy.AgnosticEnergyStack
import site.siredvin.broccolium.modules.storage.energy.Energies
import site.siredvin.broccolium.modules.storage.fluid.AgnosticFluidStack
import site.siredvin.broccolium.modules.storage.item.ContainerWrapper
import site.siredvin.testiarium.api.GameTest
import site.siredvin.testiarium.api.TestGroup
import site.siredvin.testiarium.api.immediate
import java.util.WeakHashMap

object ForgeProviders {
    private class Stores {
        val items = ItemStacksResourceHandler(2)
        val fluid = FluidStacksResourceHandler(1, 2000)
        val energy = SimpleEnergyHandler(100)
    }
    private val stores = WeakHashMap<ObservedEntity, Stores>()
    private fun of(entity: ObservedEntity) = stores.getOrPut(entity) { Stores() }

    @JvmStatic
    fun register(event: RegisterCapabilitiesEvent) {
        event.registerBlockEntity(Capabilities.Item.BLOCK, NativeFixtures.type.get()) { entity, side ->
            if (side == Direction.DOWN) null else of(entity).items
        }
        event.registerBlockEntity(Capabilities.Fluid.BLOCK, NativeFixtures.type.get()) { entity, side ->
            if (side == Direction.DOWN) null else of(entity).fluid
        }
        event.registerBlockEntity(Capabilities.Energy.BLOCK, NativeFixtures.type.get()) { entity, side ->
            if (side == Direction.DOWN) null else of(entity).energy
        }
    }
}

@TestGroup("broccolium_native")
class ForgeProviderGameTests {
    @GameTest(template = "empty")
    fun completePlayerEquipmentRetainsNativeRulesAndNestedRollback(helper: GameTestHelper) = helper.immediate {
        @Suppress("DEPRECATION")
        val player = helper.makeMockServerPlayerInLevel()
        try {
            NativeAccessAssertions.completePlayerRollback(player)
        } finally {
            player.inventory.clearContent()
            player.discard()
        }
    }

    @GameTest(template = "empty")
    fun worldPositiveProvidersConserveAndRejectRemoved(helper: GameTestHelper) = helper.immediate {
        val pos = helper.absolutePos(BlockPos(1, 1, 1))
        helper.level.setBlockAndUpdate(pos, NativeFixtures.block.get().defaultBlockState())
        val entity = checkNotNull(helper.level.getBlockEntity(pos))
        for (side in listOf(null, Direction.UP)) {
            val items = checkNotNull(ForgeStorageUtils.extractStorageFromBlock(helper.level, pos, entity, side))
            val stack = ItemStack(Items.DIAMOND, 5).also { it.set(DataComponents.CUSTOM_NAME, Component.literal("world variant")) }
            check(items.store(stack, true).isEmpty && items.get(0).isEmpty)
            check(items.store(stack, false).isEmpty)
            val taken = items.take({ true }, Int.MAX_VALUE, false)
            check(ItemStack.matches(stack, taken) && items.get(0).isEmpty)
            val fluid = checkNotNull(ForgeStorageUtils.extractFluidStorageFromBlock(helper.level, pos, entity, side))
            check(fluid.store(AgnosticFluidStack(Fluids.WATER, 2500.0), true).amount == 500.0)
            check(fluid.getContent().asSequence().sumOf { it.amount } == 0.0)
            check(fluid.store(AgnosticFluidStack(Fluids.WATER, 2500.0), false).amount == 500.0)
            check(fluid.take({ true }, 3000.0, false).amount == 2000.0)
            val energy = checkNotNull(ForgeStorageUtils.extractEnergyStorageFromBlock(helper.level, pos, entity, side))
            check(energy.store(AgnosticEnergyStack(Energies.FORGE, Long.MAX_VALUE), true).amount == Long.MAX_VALUE - 100)
            check(energy.getContent().asSequence().sumOf { it.amount } == 0L)
            check(energy.store(AgnosticEnergyStack(Energies.FORGE, 150), false).amount == 50L)
            check(energy.take({ true }, Long.MAX_VALUE, false).amount == 100L)
            for (invalid in listOf(-1.0, Double.NaN, Double.POSITIVE_INFINITY)) {
                try {
                    fluid.take({ true }, invalid, false)
                    error("Invalid native fluid limit accepted")
                } catch (_: IllegalArgumentException) {
                    check(fluid.getContent().asSequence().sumOf { it.amount } == 0.0)
                }
            }
            try {
                items.take(1, -1, 0, { true }, false)
                error("Invalid native item slot accepted")
            } catch (_: IllegalArgumentException) {
                check(items.getContent().asSequence().all { it.isEmpty })
            }
            try {
                energy.take({ true }, -1, false)
                error("Invalid native energy limit accepted")
            } catch (_: IllegalArgumentException) {
                check(energy.firstEnergy.amount == 0L)
            }
        }
        check(ForgeStorageUtils.extractStorageFromBlock(helper.level, pos, entity, Direction.DOWN) == null)
        check(ForgeStorageUtils.extractFluidStorageFromBlock(helper.level, pos, entity, Direction.DOWN) == null)
        check(ForgeStorageUtils.extractEnergyStorageFromBlock(helper.level, pos, entity, Direction.DOWN) == null)
        val native = checkNotNull(helper.level.getCapability(Capabilities.Fluid.BLOCK, pos, null))
        Transaction.openRoot().use { outer ->
            check(native.insert(0, FluidResource.of(Fluids.WATER), 1000, outer) == 1000)
            Transaction.open(outer).use { nested ->
                check(native.extract(0, FluidResource.of(Fluids.WATER), 700, nested) == 700)
                nested.commit()
            }
            check(native.getAmountAsLong(0) == 300L)
        }
        check(native.getAmountAsLong(0) == 0L)
        helper.level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState())
        check(ForgeStorageUtils.extractStorageFromBlock(helper.level, pos, entity, null) == null)
        check(ForgeStorageUtils.extractFluidStorageFromBlock(helper.level, pos, entity, null) == null)
        check(ForgeStorageUtils.extractEnergyStorageFromBlock(helper.level, pos, entity, null) == null)
    }

    @GameTest(template = "empty")
    fun nativeBucketProviderReplacesWorldOriginAndRejectsUnsupported(helper: GameTestHelper) = helper.immediate {
        val pos = helper.absolutePos(BlockPos(1, 1, 1))
        helper.level.setBlockAndUpdate(pos, Blocks.CHEST.defaultBlockState())
        val chest = helper.level.getBlockEntity(pos) as net.minecraft.world.Container
        chest.setItem(0, ItemStack(Items.BUCKET))
        val origin = ContainerWrapper(chest)
        NativeAccessAssertions.nestedBucketRollback(origin)
        val fluid = checkNotNull(ForgeStorageUtils.extractFluidStorageFromItem(helper.level, origin, 0))
        check(fluid.store(AgnosticFluidStack(Fluids.WATER, 1000.0), true).isEmpty)
        check(chest.getItem(0).`is`(Items.BUCKET))
        check(fluid.store(AgnosticFluidStack(Fluids.WATER, 1000.0), false).isEmpty)
        check(chest.getItem(0).`is`(Items.WATER_BUCKET) && chest.getItem(0).count == 1)
        val filled = checkNotNull(ForgeStorageUtils.extractFluidStorageFromItem(helper.level, origin, 0))
        check(filled.take({ true }, 1000.0, true).amount == 1000.0)
        check(chest.getItem(0).`is`(Items.WATER_BUCKET))
        check(filled.take({ true }, 1000.0, false).amount == 1000.0)
        check(chest.getItem(0).`is`(Items.BUCKET))
        val unsupported = object : SlottedAgnosticStorage<ItemStack, Int> by origin {}
        try {
            ForgeStorageUtils.extractFluidStorageFromItem(helper.level, unsupported, 0)
            error("Unsupported backing accepted")
        } catch (expected: UnsupportedOperationException) {
            check(expected.message?.contains("ReplaceableItemStorage") == true)
        }
        check(chest.getItem(0).`is`(Items.BUCKET) && chest.getItem(0).count == 1)
    }
}
