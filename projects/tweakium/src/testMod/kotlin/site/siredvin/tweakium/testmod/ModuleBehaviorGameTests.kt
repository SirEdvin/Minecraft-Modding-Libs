package site.siredvin.tweakium.testmod

import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.peripheral.IComputerAccess
import dan200.computercraft.api.peripheral.IPeripheral
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.gametest.framework.GameTest
import net.minecraft.gametest.framework.GameTestHelper
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.ChestBlockEntity
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3
import site.siredvin.broccolium.modules.lookup.BaseLookup
import site.siredvin.broccolium.modules.platform.PlatformRegistries
import site.siredvin.broccolium.modules.storage.item.ContainerWrapper
import site.siredvin.broccolium.modules.tricks.DropConsumer
import site.siredvin.testiarium.api.TestGroup
import site.siredvin.testiarium.api.immediate
import site.siredvin.tweakium.modules.platform.ComputerPlatformToolkit
import site.siredvin.tweakium.modules.player.FakePlayerProxy
import site.siredvin.tweakium.modules.plugins.PeripheralPluginUtils
import site.siredvin.tweakium.modules.plugins.SuppliedInventoryPlugin
import java.lang.reflect.Proxy
import java.util.Optional

@TestGroup("tweakium")
class ModuleBehaviorGameTests {
    @GameTest(template = "modulebehavior.empty")
    fun nativeRegistriesRoundTripWithoutInventingMissingKeys(helper: GameTestHelper) = helper.immediate {
        val registry = PlatformRegistries.ITEMS
        val key = registry.getKey(Items.DIAMOND)
        check(key.toString() == "minecraft:diamond")
        check(registry.get(key) === Items.DIAMOND)
        check(registry.get(registry.getId(Items.DIAMOND)) === Items.DIAMOND)
        check(key in registry.keySet())
        val missing = key.withPath("not_a_real_item_for_testiarium")
        check(registry.tryGet(key) === Items.DIAMOND)
        check(missing !in registry.keySet())
        check(PlatformRegistries.BLOCKS.get(PlatformRegistries.BLOCKS.getKey(Blocks.CHEST)) === Blocks.CHEST)
    }

    @GameTest(template = "modulebehavior.empty")
    fun lookupUsesFirstNonNullResultAndForwardsNativeContext(helper: GameTestHelper) = helper.immediate {
        val lookup = BaseLookup<String>()
        val position = helper.absolutePos(BlockPos(1, 1, 1))
        val calls = mutableListOf<Int>()
        lookup.addBlockLookup { level, pos, entity, direction ->
            check(level === helper.level && pos == position && entity == null && direction == Direction.WEST)
            calls += 1
            null
        }
        lookup.addBlockLookup { _, _, _, _ ->
            calls += 2
            "oak"
        }
        lookup.addBlockLookup { _, _, _, _ -> error("Lookup must short circuit") }
        check(lookup.extractFromBlock(helper.level, position, null, Direction.WEST) == "oak")
        check(calls == listOf(1, 2))
        check(lookup.extractFromUnknown(helper.level, null, null) == null)
        try {
            lookup.extractFromUnknown(helper.level, "unsupported", null)
            error("Unsupported lookup target must fail")
        } catch (_: IllegalArgumentException) { }
    }

    @GameTest(template = "modulebehavior.empty")
    fun itemAndBlockQueriesEvaluateNativeRegistryValues(helper: GameTestHelper) = helper.immediate {
        val query = mapOf("or" to mapOf(1 to mapOf("name" to "minecraft:diamond"), 2 to mapOf("name" to "minecraft:gold_ingot")))
        val predicate = PeripheralPluginUtils.itemQueryToPredicate(query)
        check(predicate.test(ItemStack(Items.DIAMOND)))
        check(predicate.test(ItemStack(Items.GOLD_INGOT)))
        check(!predicate.test(ItemStack(Items.DIRT)))
        val negated = PeripheralPluginUtils.blockQueryToPredicate(mapOf("not" to mapOf("name" to "minecraft:stone")))
        check(!negated.test(Blocks.STONE.defaultBlockState()))
        check(negated.test(Blocks.DIRT.defaultBlockState()))
        try {
            PeripheralPluginUtils.itemQueryToPredicate("minecraft:not_an_item")
            error("Missing registry item must fail")
        } catch (_: LuaException) { }
    }

    private fun computerFor(target: ChestBlockEntity): IComputerAccess {
        val peripheral = object : IPeripheral {
            override fun getType() = "inventory"
            override fun getTarget(): Any = target
            override fun equals(other: IPeripheral?) = other === this
        }
        return Proxy.newProxyInstance(IComputerAccess::class.java.classLoader, arrayOf(IComputerAccess::class.java)) { _, method, args ->
            check(method.name == "getAvailablePeripheral" && args?.get(0) == "source")
            peripheral
        } as IComputerAccess
    }

    @GameTest(template = "modulebehavior.empty")
    fun inventoryQueryPullHonorsDestinationAndConservesItems(helper: GameTestHelper) = helper.immediate {
        val pos = BlockPos(1, 1, 1)
        helper.setBlock(pos, Blocks.CHEST)
        val source = helper.getBlockEntity(pos) as ChestBlockEntity
        source.setItem(0, ItemStack(Items.DIAMOND, 12))
        val destination = SimpleContainer(3)
        val plugin = SuppliedInventoryPlugin({ helper.level }, { ContainerWrapper(destination) }, 4)
        val moved = plugin.pullItems(computerFor(source), "source", "minecraft:diamond", Optional.of(9), Optional.of(2))
        check(moved == 4) { "Transfer limit must cap query pull" }
        check(destination.getItem(0).isEmpty) { "Query pull ignored destination slot" }
        check(destination.getItem(1).count == 4)
        check(source.getItem(0).count == 8)
        check(source.getItem(0).count + destination.getItem(1).count == 12)
    }

    @GameTest(template = "modulebehavior.empty")
    fun numericPullRejectsOutOfRangeDestinationBeforeMutation(helper: GameTestHelper) = helper.immediate {
        val pos = BlockPos(1, 1, 1)
        helper.setBlock(pos, Blocks.CHEST)
        val source = helper.getBlockEntity(pos) as ChestBlockEntity
        source.setItem(0, ItemStack(Items.DIAMOND, 12))
        val destination = SimpleContainer(3)
        val plugin = SuppliedInventoryPlugin({ helper.level }, { ContainerWrapper(destination) }, 4)
        for (slot in listOf(0, 4)) {
            try {
                plugin.pullItems(computerFor(source), "source", 1, Optional.of(4), Optional.of(slot))
                error("Invalid destination $slot must fail")
            } catch (_: LuaException) { }
        }
        check(source.getItem(0).count == 12)
        check(destination.isEmpty)
    }

    @GameTest(template = "modulebehavior.empty")
    fun fakePlayerMissAndUnbreakableBlockDoNotMutateWorld(helper: GameTestHelper) = helper.immediate {
        val player = ComputerPlatformToolkit.get().createFakePlayer(helper.level, FakePlayerProxy.DUMMY_PROFILE)
        val origin = helper.absolutePos(BlockPos(1, 2, 1))
        player.moveTo(origin.x + 0.5, origin.y.toDouble(), origin.z + 0.5, 0f, 0f)
        val proxy = FakePlayerProxy(player)
        check(proxy.findHit(skipEntity = true, skipBlock = true).type == HitResult.Type.MISS)
        check(!proxy.swing(true, true, null).first)
        val position = helper.absolutePos(BlockPos(1, 1, 1))
        helper.level.setBlockAndUpdate(position, Blocks.BEDROCK.defaultBlockState())
        val hit = BlockHitResult(Vec3.atLowerCornerOf(position), Direction.UP, position, false)
        check(proxy.swingBlock(hit) == (false to "Unbreakable block detected"))
        check(helper.level.getBlockState(position).`is`(Blocks.BEDROCK))
    }

    @GameTest(template = "modulebehavior.empty")
    fun failedPlayerActionAlwaysReleasesGlobalDropCapture(helper: GameTestHelper) = helper.immediate {
        val player = ComputerPlatformToolkit.get().createFakePlayer(helper.level, FakePlayerProxy.DUMMY_PROFILE)
        val position = helper.absolutePos(BlockPos(1, 2, 1))
        player.moveTo(position.x.toDouble(), position.y.toDouble(), position.z.toDouble())
        val proxy = FakePlayerProxy(player)
        val sentinel = IllegalStateException("action failed")
        // Exercise both overloads: neither may leak process-global capture after an exception.
        for (entityMode in listOf(false, true)) {
            try {
                if (entityMode) proxy.withConsumer(player) { throw sentinel } else proxy.withConsumer(helper.level, position) { throw sentinel }
                error("Action must propagate its failure")
            } catch (exception: IllegalStateException) {
                check(exception === sentinel)
            }
            val drop = ItemEntity(helper.level, position.x.toDouble(), position.y.toDouble(), position.z.toDouble(), ItemStack(Items.DIAMOND))
            val captured = DropConsumer.onEntitySpawn(drop)
            // Reset only if the regression leaked state, so other GameTests stay isolated.
            if (captured) DropConsumer.reset()
            check(!captured) { "Failed action left global drop capture enabled" }
        }
    }
}
