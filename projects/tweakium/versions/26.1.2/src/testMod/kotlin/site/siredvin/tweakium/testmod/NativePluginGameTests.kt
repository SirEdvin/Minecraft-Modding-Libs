package site.siredvin.tweakium.testmod

import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.api.lua.MethodResult
import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.api.turtle.TurtleSide
import dan200.computercraft.api.upgrades.UpgradeData
import dan200.computercraft.shared.ModRegistry
import dan200.computercraft.shared.turtle.blocks.TurtleBlockEntity
import dan200.computercraft.shared.turtle.core.TurtleBrain
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.gametest.framework.GameTestHelper
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import site.siredvin.broccolium.modules.storage.fluid.AgnosticFluidStorageLookup
import site.siredvin.broccolium.modules.storage.fluid.EmptyAgnosticFluidStorage
import site.siredvin.broccolium.modules.storage.fluid.api.AgnosticFluidStorage
import site.siredvin.broccolium.modules.storage.item.ContainerWrapper
import site.siredvin.testiarium.api.GameTest
import site.siredvin.testiarium.api.TestGroup
import site.siredvin.testiarium.api.thenExecuteFailFast
import site.siredvin.testiarium.cct.thenLua
import site.siredvin.tweakium.modules.peripheral.OwnedPeripheral
import site.siredvin.tweakium.modules.peripheral.api.IPeripheralOperation
import site.siredvin.tweakium.modules.peripheral.boon.OperationBoon
import site.siredvin.tweakium.modules.peripheral.boon.PeripheralOwnerBoonKey
import site.siredvin.tweakium.modules.peripheral.owner.TurtlePeripheralOwner
import site.siredvin.tweakium.modules.plugins.AbstractFluidStoragePlugin
import site.siredvin.tweakium.modules.plugins.FullEnergyPlugin
import site.siredvin.tweakium.modules.plugins.InventoryPlugin
import site.siredvin.tweakium.modules.plugins.ItemStoragePlugin
import site.siredvin.tweakium.modules.storage.energy.TurtleAgnosticEnergyStorage
import java.util.Collections
import java.util.WeakHashMap

object NativePluginFixtures {
    // Test-only block provider: expose the native bucket capability in slot0.
    // No fabricated transfer implementation; both loaders replace origin slots.
    val bucketTargets: MutableSet<net.minecraft.world.level.block.entity.BlockEntity> = Collections.newSetFromMap(WeakHashMap())
    val enabledTurtles: MutableSet<dan200.computercraft.api.turtle.ITurtleAccess> = Collections.newSetFromMap(WeakHashMap())
    private var installed = false

    @JvmStatic
    fun install() {
        if (installed) return
        installed = true
        AgnosticFluidStorageLookup.addBlockLookup { level, _, entity, _ ->
            if (entity in bucketTargets && entity is net.minecraft.world.Container) {
                AgnosticFluidStorageLookup.extractFromInventoryStack(level, ContainerWrapper(entity), 0)
            } else {
                null
            }
        }
    }
}

class NativePluginPeripheral(owner: TurtlePeripheralOwner) : OwnedPeripheral<TurtlePeripheralOwner>("r3_native", owner) {
    override val isEnabled = true
    private val paid = object : IPeripheralOperation<Boolean> {
        override val name = "R3_PAID"
        override fun getCost(context: Boolean): Int = 7
        override fun getCooldown(context: Boolean): Int = 60_000
        override fun computerDescription(): Map<String, Any> = emptyMap()
    }
    private val expensive = object : IPeripheralOperation<Boolean> {
        override val name = "R3_EXPENSIVE"
        override fun getCost(context: Boolean): Int = 1000
        override fun getCooldown(context: Boolean): Int = 0
        override fun computerDescription(): Map<String, Any> = emptyMap()
    }

    init {
        owner.attachFuel()
        owner.attachBoon(PeripheralOwnerBoonKey.OPERATION, OperationBoon(owner))
        owner.getBoon(PeripheralOwnerBoonKey.OPERATION)!!.registerOperation(paid)
        owner.getBoon(PeripheralOwnerBoonKey.OPERATION)!!.registerOperation(expensive)
        addPlugin(InventoryPlugin(checkNotNull(owner.level), owner.storage, 4))
        addPlugin(ItemStoragePlugin(owner.storage, checkNotNull(owner.level), 4))
        addPlugin(FullEnergyPlugin(checkNotNull(owner.level), TurtleAgnosticEnergyStorage(owner.turtle), 25))
        addPlugin(object : AbstractFluidStoragePlugin(checkNotNull(owner.level), 1000.0) {
            override val storage: AgnosticFluidStorage
                get() = AgnosticFluidStorageLookup.extractFromInventoryStack(checkNotNull(owner.level), owner.storage, 0) ?: EmptyAgnosticFluidStorage
        })
    }

    @LuaFunction(mainThread = true)
    fun spend(allowed: Boolean): MethodResult = runPaid(paid, allowed)

    @LuaFunction(mainThread = true)
    fun spendInsufficient(): MethodResult = runPaid(expensive, true)

    private fun runPaid(operation: IPeripheralOperation<Boolean>, allowed: Boolean): MethodResult = peripheralOwner.withOperation(operation, allowed, {
        val data = peripheralOwner.dataStorage
        data.putInt("mutations", data.getInt("mutations") + 1)
        MethodResult.of(data.getInt("mutations"))
    }, check = { if (it) null else MethodResult.of(null, "permission denied") })
}

@TestGroup("tweakium")
class NativePluginGameTests {
    @GameTest(template = "modulebehavior.empty", timeoutTicks = 500)
    fun realLuaPluginQueriesTransfersRefusalsAndPaidOperations(helper: GameTestHelper) {
        val pos = helper.absolutePos(BlockPos(1, 1, 1))
        val targetPos = pos.south()
        val state = ModRegistry.Blocks.TURTLE_NORMAL.get().defaultBlockState()
        helper.level.setBlockAndUpdate(pos, state)
        helper.level.setBlockAndUpdate(targetPos, state)
        val entity = helper.level.getBlockEntity(pos) as TurtleBlockEntity
        val targetEntity = helper.level.getBlockEntity(targetPos) as TurtleBlockEntity
        val turtle = entity.access as TurtleBrain
        val target = targetEntity.access as TurtleBrain
        helper.runBeforeTestEnd {
            NativePluginFixtures.enabledTurtles.remove(turtle)
            NativePluginFixtures.bucketTargets.remove(targetEntity)
            turtle.inventory.clearContent()
            target.inventory.clearContent()
            helper.level.setBlockAndUpdate(pos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState())
            helper.level.setBlockAndUpdate(targetPos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState())
        }
        turtle.direction = Direction.SOUTH
        turtle.fuelLevel = 100
        target.fuelLevel = 0
        turtle.inventory.setItem(0, ItemStack(Items.WATER_BUCKET))
        turtle.inventory.setItem(1, ItemStack(Items.DIAMOND, 12))
        target.inventory.setItem(0, ItemStack(Items.BUCKET))
        NativePluginFixtures.enabledTurtles.add(turtle)
        NativePluginFixtures.bucketTargets.add(targetEntity)
        val holder = helper.level.registryAccess().lookupOrThrow(ITurtleUpgrade.REGISTRY).getOrThrow(ResourceKey.create(ITurtleUpgrade.REGISTRY, NativeUpgradeFixtures.ID))
        entity.setComputerID(1)
        entity.setLabel("nativeplugins.transfers")
        turtle.setUpgrade(TurtleSide.LEFT, UpgradeData.ofDefault(holder))
        entity.createServerComputer().turnOn()
        helper.thenLua("nativeplugins.transfers").thenExecuteFailFast {
            check(turtle.inventory.getItem(0).`is`(Items.WATER_BUCKET) && target.inventory.getItem(0).`is`(Items.BUCKET))
            check(turtle.inventory.getItem(1).count == 8 && turtle.inventory.getItem(2).count == 2 && target.inventory.getItem(1).count == 2)
            check(turtle.fuelLevel == 77 && target.fuelLevel == 16) { "Lua energy/paid-operation accounting differs from native state" }
        }.thenSucceed()
    }
}
