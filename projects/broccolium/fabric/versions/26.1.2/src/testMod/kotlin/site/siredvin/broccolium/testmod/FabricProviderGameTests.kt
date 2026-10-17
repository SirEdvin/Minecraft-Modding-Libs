package site.siredvin.broccolium.testmod

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.fabricmc.fabric.api.transfer.v1.item.ContainerStorage
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.gametest.framework.GameTestHelper
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.material.Fluids
import site.siredvin.broccolium.integrations.team_reborn_energy.Integration
import site.siredvin.broccolium.modules.storage.FabricStorageUtils
import site.siredvin.broccolium.modules.storage.energy.AgnosticEnergyStack
import site.siredvin.broccolium.modules.storage.energy.Energies
import site.siredvin.broccolium.modules.storage.fluid.AgnosticFluidStack
import site.siredvin.broccolium.modules.storage.item.ContainerWrapper
import site.siredvin.testiarium.api.GameTest
import site.siredvin.testiarium.api.TestGroup
import site.siredvin.testiarium.api.immediate
import team.reborn.energy.api.EnergyStorage
import team.reborn.energy.api.base.SimpleEnergyStorage
import java.util.WeakHashMap

object FabricProviders {
    class Stores {
        val fluid = object : SingleVariantStorage<FluidVariant>() {
            override fun getBlankVariant(): FluidVariant = FluidVariant.blank()
            override fun getCapacity(variant: FluidVariant): Long = 162000
        }
        val energy = SimpleEnergyStorage(100, 100, 100)
    }
    private val stores = WeakHashMap<ObservedEntity, Stores>()
    fun of(entity: ObservedEntity): Stores = stores.getOrPut(entity) { Stores() }

    fun register() {
        ItemStorage.SIDED.registerForBlockEntity({ entity, side ->
            if (side == Direction.DOWN) null else ContainerStorage.of(entity.cargo, side)
        }, NativeFixtures.type.get())
        FluidStorage.SIDED.registerForBlockEntity({ entity, side ->
            if (side == Direction.DOWN) null else of(entity).fluid
        }, NativeFixtures.type.get())
        EnergyStorage.SIDED.registerForBlockEntity({ entity, side ->
            if (side == Direction.DOWN) null else of(entity).energy
        }, NativeFixtures.type.get())
    }
}

@TestGroup("broccolium_native")
class FabricProviderGameTests {
    @GameTest(template = "empty")
    fun worldPositiveFluidEnergyProvidersAndNestedRollback(helper: GameTestHelper) = helper.immediate {
        val pos = helper.absolutePos(BlockPos(1, 1, 1))
        helper.level.setBlockAndUpdate(pos, NativeFixtures.block.get().defaultBlockState())
        val entity = helper.level.getBlockEntity(pos) as ObservedEntity
        val fluid = checkNotNull(FabricStorageUtils.extractFluidStorage(helper.level, pos, entity, null))
        check(fluid.store(AgnosticFluidStack(Fluids.WATER, 2500.0), true).amount == 500.0)
        check(FabricProviders.of(entity).fluid.amount == 0L)
        check(fluid.store(AgnosticFluidStack(Fluids.WATER, 2500.0), false).amount == 500.0)
        check(FabricProviders.of(entity).fluid.amount == 162000L)
        check(fluid.take({ true }, 3000.0, true).amount == 2000.0)
        check(FabricProviders.of(entity).fluid.amount == 162000L)
        check(fluid.take({ true }, 3000.0, false).amount == 2000.0)
        val native = FabricProviders.of(entity).fluid
        Transaction.openOuter().use { outer ->
            check(native.insert(FluidVariant.of(Fluids.WATER), 81000, outer) == 81000L)
            outer.openNested().use { nested ->
                check(native.extract(FluidVariant.of(Fluids.WATER), 56700, nested) == 56700L)
                nested.commit()
            }
            check(native.amount == 24300L)
        }
        check(native.amount == 0L)
        val energy = checkNotNull(Integration.extractEnergyStorage(helper.level, pos, entity, null))
        check(energy.store(AgnosticEnergyStack(Energies.REDSTONE_FLUX, 150), true).amount == 50L)
        check(FabricProviders.of(entity).energy.amount == 0L)
        check(energy.store(AgnosticEnergyStack(Energies.REDSTONE_FLUX, 150), false).amount == 50L)
        check(energy.take({ true }, Long.MAX_VALUE, true).amount == 100L)
        check(FabricProviders.of(entity).energy.amount == 100L)
        check(energy.take({ true }, Long.MAX_VALUE, false).amount == 100L)
        check(FabricProviders.of(entity).energy.amount == 0L)
        checkNotNull(FabricStorageUtils.extractStorage(helper.level, pos, entity, Direction.UP))
        checkNotNull(FabricStorageUtils.extractFluidStorage(helper.level, pos, entity, Direction.UP))
        checkNotNull(Integration.extractEnergyStorage(helper.level, pos, entity, Direction.UP))
    }

    @GameTest(template = "empty")
    fun worldBucketReplacementConservesNativeUnits(helper: GameTestHelper) = helper.immediate {
        val pos = helper.absolutePos(BlockPos(1, 1, 1))
        helper.level.setBlockAndUpdate(pos, Blocks.CHEST.defaultBlockState())
        val chest = helper.level.getBlockEntity(pos) as net.minecraft.world.Container
        chest.setItem(0, ItemStack(Items.BUCKET))
        val origin = ContainerWrapper(chest)
        val storage = checkNotNull(FabricStorageUtils.extractFluidStorageFromItem(helper.level, origin, 0))
        check(storage.store(AgnosticFluidStack(Fluids.WATER, 1000.0), true).isEmpty)
        check(chest.getItem(0).`is`(Items.BUCKET))
        check(storage.store(AgnosticFluidStack(Fluids.WATER, 1000.0), false).isEmpty)
        check(chest.getItem(0).`is`(Items.WATER_BUCKET) && chest.getItem(0).count == 1)
        val filled = checkNotNull(FabricStorageUtils.extractFluidStorageFromItem(helper.level, origin, 0))
        check(filled.take({ true }, 1000.0, true).amount == 1000.0)
        check(chest.getItem(0).`is`(Items.WATER_BUCKET))
        check(filled.take({ true }, 1000.0, false).amount == 1000.0)
        check(chest.getItem(0).`is`(Items.BUCKET) && chest.getItem(0).count == 1)
    }
}
