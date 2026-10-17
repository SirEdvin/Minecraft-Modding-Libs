package site.siredvin.tweakium.testmod

import com.mojang.authlib.GameProfile
import dan200.computercraft.api.lua.ILuaContext
import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.api.lua.ObjectArguments
import dan200.computercraft.api.peripheral.IComputerAccess
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
//? if <26.1 {
import net.minecraft.gametest.framework.GameTest
//?} else {
/*import site.siredvin.testiarium.api.GameTest
*///?}
import net.minecraft.gametest.framework.GameTestHelper
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3
import site.siredvin.broccolium.modules.tricks.DropConsumer
import site.siredvin.testiarium.api.TestGroup
import site.siredvin.testiarium.api.immediate
import site.siredvin.tweakium.modules.operation.SphereOperationContext
import site.siredvin.tweakium.modules.peripheral.OwnedPeripheral
import site.siredvin.tweakium.modules.peripheral.api.IExpandedPeripheral
import site.siredvin.tweakium.modules.peripheral.api.IPeripheralOperation
import site.siredvin.tweakium.modules.peripheral.api.IPeripheralOwner
import site.siredvin.tweakium.modules.peripheral.api.IPeripheralPlugin
import site.siredvin.tweakium.modules.peripheral.boon.OperationBoon
import site.siredvin.tweakium.modules.peripheral.boon.PeripheralOwnerBoonKey
import site.siredvin.tweakium.modules.peripheral.boon.ScanningBoon
import site.siredvin.tweakium.modules.peripheral.owner.DisabledPeripheralOwner
import site.siredvin.tweakium.modules.peripheral.util.CompoundTagDataStorage
import site.siredvin.tweakium.modules.platform.ComputerPlatformToolkit
import site.siredvin.tweakium.modules.player.FakePlayerProxy
import java.lang.reflect.Proxy
import java.util.UUID
import java.util.function.BiConsumer

@TestGroup("tweakium")
class RuntimeBehaviorGameTests {
    class EchoPlugin : IPeripheralPlugin {
        override var connectedPeripheral: IExpandedPeripheral? = null
        override val additionalType = "foliage"

        @LuaFunction
        fun echo(value: String): String = "leaf:$value"
    }

    @GameTest(template = "modulebehavior.empty")
    fun nativeMethodDiscoveryBindsAndInvokesPluginsExactlyOnce(helper: GameTestHelper) = helper.immediate {
        val plugin = EchoPlugin()
        val peripheral = object : OwnedPeripheral<DisabledPeripheralOwner>("test_tree", DisabledPeripheralOwner()) {
            override val isEnabled = true
        }
        peripheral.addPlugin(plugin)
        val methods = peripheral.methodNames
        check(methods.count { it == "echo" } == 1)
        check(peripheral.methodNames.contentEquals(methods))
        check(plugin.connectedPeripheral === peripheral)
        check("foliage" in peripheral.additionalTypes)
        val result = peripheral.callMethod(unused(IComputerAccess::class.java), unused(ILuaContext::class.java), methods.indexOf("echo"), ObjectArguments("oak"))
        check(result.result!!.contentEquals(arrayOf("leaf:oak")))
    }

    @GameTest(template = "modulebehavior.empty")
    fun scanningUsesNativeFiltersBoundsAndOperationPolicy(helper: GameTestHelper) = helper.immediate {
        val center = BlockPos(1, 1, 1)
        helper.setBlock(center.above(), Blocks.DIAMOND_BLOCK)
        helper.setBlock(center.east(2), Blocks.DIAMOND_BLOCK)
        val owner = object : IPeripheralOwner by DisabledPeripheralOwner() {
            override val level = helper.level
            override val pos = helper.absolutePos(center)
            override val facing = Direction.NORTH
            override val dataStorage = CompoundTagDataStorage(CompoundTag()) {}
        }
        owner.attachBoon(PeripheralOwnerBoonKey.OPERATION, OperationBoon(owner))
        var cost = 0
        val costContexts = mutableListOf<Int>()
        val operation = object : IPeripheralOperation<SphereOperationContext> {
            override val name = "SCAN_TEST"
            override fun getCooldown(context: SphereOperationContext) = 0
            override fun getCost(context: SphereOperationContext): Int {
                costContexts += context.radius
                return cost
            }
            override fun computerDescription() = emptyMap<String, Any>()
        }
        val scanner = ScanningBoon(owner, 2).attachBlockScan(operation, BiConsumer { _, data -> data["enriched"] = true })
        val results = scanner.scan(ObjectArguments("block", 1, "minecraft:diamond_block")).result!!.single() as List<*>
        val found = results.single() as Map<*, *>
        check(found["name"] == "minecraft:diamond_block" && found["enriched"] == true)
        check(found["x"] == 0 && found["y"] == 1 && found["z"] == 0)
        check(costContexts == listOf(1))
        check((scanner.scan(ObjectArguments("block", 2, "minecraft:diamond_block")).result!!.single() as List<*>).size == 2)
        for (arguments in listOf(ObjectArguments("block", 0), ObjectArguments("block", 3), ObjectArguments("unknown", 1))) {
            try {
                scanner.scan(arguments)
                error("Invalid scan must be rejected")
            } catch (_: LuaException) { }
        }
        cost = 1
        val rejected = scanner.scan(ObjectArguments("block", 1, "minecraft:diamond_block")).result!!
        check(rejected[0] == null && rejected[1] is String) { "Missing fuel must reject scanning" }
        check(helper.getBlockState(center.above()).`is`(Blocks.DIAMOND_BLOCK))
    }

    @GameTest(template = "modulebehavior.empty")
    fun successfulFakePlayerBreakCapturesExactlyOneDrop(helper: GameTestHelper) = helper.immediate {
        val position = helper.absolutePos(BlockPos(1, 1, 1))
        helper.level.setBlockAndUpdate(position, Blocks.DIRT.defaultBlockState())
        val player = ComputerPlatformToolkit.get().createFakePlayer(helper.level, GameProfile(UUID.randomUUID(), "BreakFixture"))
        player.setPos(position.x.toDouble(), position.y.toDouble(), position.z.toDouble())
        val proxy = FakePlayerProxy(player)
        try {
            val hit = BlockHitResult(Vec3.atLowerCornerOf(position), Direction.UP, position, false)
            repeat(3) { if (!helper.level.isEmptyBlock(position)) check(proxy.swingBlock(hit).first) }
            check(helper.level.isEmptyBlock(position)) { "Fake player did not destroy the block" }
            check((0 until player.inventory.containerSize).map { player.inventory.getItem(it) }.filter { it.`is`(Items.DIRT) }.sumOf { it.count } == 1) { "Block drop was lost or duplicated" }
        } finally {
            player.inventory.clearContent()
        }
    }

    @GameTest(template = "modulebehavior.empty")
    fun livingDropCaptureTargetsOnlyConfiguredEntityAndPreservesRemainder(helper: GameTestHelper) = helper.immediate {
        val position = helper.absolutePos(BlockPos(1, 1, 1))
        val target = ItemEntity(helper.level, position.x.toDouble(), position.y.toDouble(), position.z.toDouble(), ItemStack(Items.DIAMOND))
        val other = ItemEntity(helper.level, position.x.toDouble(), position.y.toDouble(), position.z.toDouble(), ItemStack(Items.DIAMOND))
        var consumed = 0
        DropConsumer.configure(target, { stack ->
            consumed += 1
            stack.copyWithCount(stack.count - 1)
        })
        val remaining: List<ItemStack>
        try {
            check(!DropConsumer.onLivingDrop(other, ItemStack(Items.DIAMOND, 3)))
            check(DropConsumer.onLivingDrop(target, ItemStack(Items.DIAMOND, 3))) { "Configured entity drops were not captured" }
            check(consumed == 1)
        } finally {
            remaining = DropConsumer.reset()
        }
        check(remaining.size == 1 && remaining.single().count == 2 && remaining.single().`is`(Items.DIAMOND))
        check(!DropConsumer.onLivingDrop(target, ItemStack(Items.DIAMOND)))
        check(!DropConsumer.onEntitySpawn(other))
    }

    private fun <T> unused(type: Class<T>): T = type.cast(Proxy.newProxyInstance(type.classLoader, arrayOf(type)) { _, method, _ -> error("Unexpected ${method.name}") })
}
