package site.siredvin.broccolium.testmod

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.gametest.framework.GameTestHelper
import net.minecraft.world.entity.EntitySpawnReason
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.chunk.LevelChunk
import net.minecraft.world.level.redstone.Orientation
import net.minecraft.world.phys.AABB
import site.siredvin.broccolium.modules.base.block.FacingBlockEntityBlock
import site.siredvin.broccolium.modules.storage.energy.AgnosticEnergyStorageLookup
import site.siredvin.broccolium.modules.storage.fluid.AgnosticFluidStorageLookup
import site.siredvin.broccolium.modules.storage.item.AgnosticItemStorageLookup
import site.siredvin.testiarium.api.GameTest
import site.siredvin.testiarium.api.TestGroup
import site.siredvin.testiarium.api.immediate

object NativeTestStructures {
    const val EMPTY_STRUCTURE = "empty"
}

@TestGroup("broccolium_native")
class NativeBehaviorGameTests {
    @GameTest(template = "empty")
    fun removedProvidersDoNotResolveReplacementOrDiscardedCargo(helper: GameTestHelper) = helper.immediate {
        val pos = helper.absolutePos(BlockPos(1, 1, 1))
        val state = NativeFixtures.block.get().defaultBlockState()
        helper.level.setBlockAndUpdate(pos, state)
        val old = checkNotNull(helper.level.getBlockEntity(pos))
        checkNotNull(AgnosticFluidStorageLookup.extractFromBlock(helper.level, pos, old, null))
        checkNotNull(AgnosticEnergyStorageLookup.extractFromBlock(helper.level, pos, old, null))
        helper.level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState())
        helper.level.setBlockAndUpdate(pos, state)
        check(old.isRemoved)
        check(AgnosticItemStorageLookup.extractFromBlock(helper.level, pos, old, null) == null)
        check(AgnosticFluidStorageLookup.extractFromBlock(helper.level, pos, old, null) == null) { "Removed fluid entity resolved replacement provider" }
        check(AgnosticEnergyStorageLookup.extractFromBlock(helper.level, pos, old, null) == null) { "Removed energy entity resolved replacement provider" }
        val cart = checkNotNull(EntityType.CHEST_MINECART.create(helper.level, EntitySpawnReason.COMMAND))
        cart.setPos(helper.absolutePos(BlockPos(1, 1, 1)).center)
        helper.level.addFreshEntity(cart)
        cart.setItem(0, ItemStack(Items.DIAMOND, 2))
        cart.discard()
        check(AgnosticItemStorageLookup.extractFromEntity(helper.level, cart, null) == null) { "Discarded entity exposed stale cargo" }
    }

    @GameTest(template = "empty")
    fun entityInventoriesExposeFirstAndLastNativeSlots(helper: GameTestHelper) = helper.immediate {
        val cart = checkNotNull(EntityType.CHEST_MINECART.create(helper.level, EntitySpawnReason.COMMAND))
        cart.setPos(helper.absolutePos(BlockPos(1, 1, 1)).center)
        helper.level.addFreshEntity(cart)
        try {
            cart.setItem(0, ItemStack(Items.DIAMOND, 2))
            cart.setItem(cart.containerSize - 1, ItemStack(Items.EMERALD, 7))
            val storage = checkNotNull(AgnosticItemStorageLookup.extractFromEntity(helper.level, cart, null))
            check(storage.getContent().asSequence().sumOf { it.count } == 9) { "Minecart lookup omitted native cargo slots" }
            check(storage.take({ it.`is`(Items.EMERALD) }, 10, false).count == 7) { "Minecart last-slot extraction failed" }
            check(cart.getItem(0).count == 2) { "Minecart unrelated first slot changed" }
        } finally {
            cart.discard()
        }
        @Suppress("DEPRECATION") // Native inventory fixture only; no login/network claim.
        val player = helper.makeMockServerPlayerInLevel()
        try {
            val inventory = player.inventory
            inventory.setItem(0, ItemStack(Items.DIAMOND, 2))
            inventory.setItem(inventory.containerSize - 1, ItemStack(Items.EMERALD, 7))
            val storage = checkNotNull(AgnosticItemStorageLookup.extractFromEntity(helper.level, player, Direction.NORTH))
            check(storage.getContent().asSequence().sumOf { it.count } == 9) { "Player lookup omitted native inventory slots (size=${inventory.containerSize})" }
            check(storage.take({ it.`is`(Items.EMERALD) }, 10, false).count == 7) { "Player last-slot extraction failed" }
            check(inventory.getItem(0).count == 2) { "Player unrelated first slot changed" }
        } finally {
            player.inventory.clearContent()
            player.discard()
        }
    }

    @GameTest(template = "empty")
    fun observerUsesNativeBlockOrientationAndRemovalOrder(helper: GameTestHelper) {
        val pos = helper.absolutePos(BlockPos(1, 1, 1))
        val block = NativeFixtures.block.get()
        val state = block.defaultBlockState()
        helper.level.setBlockAndUpdate(pos, state)
        val entity = helper.level.getBlockEntity(pos) as ObservedEntity
        entity.neighbours.clear()
        val orientation = Orientation.of(Direction.UP, Direction.NORTH, Orientation.SideBias.LEFT)
        helper.level.neighborChanged(pos, Blocks.GOLD_BLOCK, orientation)
        check(entity.neighbours.single().first === Blocks.GOLD_BLOCK)
        check(entity.neighbours.single().second === orientation)
        entity.neighbours.clear()
        helper.level.neighborChanged(pos, Blocks.IRON_BLOCK, null)
        check(entity.neighbours.single() == Blocks.IRON_BLOCK to null)
        helper.level.setBlockAndUpdate(pos, state.setValue(FacingBlockEntityBlock.FACING, Direction.EAST))
        check(helper.level.getBlockEntity(pos) === entity && entity.destroys == 0)
        helper.level.scheduleTick(pos, block, 1)
        helper.runAfterDelay(3) {
            check(entity.ticks == 1) { "Native scheduled block tick was not dispatched" }
            helper.level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState())
            check(entity.destroys == 1 && entity.removedBeforeDestroy) { "destroy must run once after native removal" }
            val drops = helper.level.getEntitiesOfClass(ItemEntity::class.java, AABB(pos).inflate(1.0))
            check(drops.filter { it.item.`is`(Items.DIAMOND) }.sumOf { it.item.count } == 4) { "Native pre-remove cargo drops lost" }
            drops.forEach { it.discard() }
            helper.level.setBlockAndUpdate(pos, Blocks.STONE.defaultBlockState())
            check(entity.destroys == 1)
            helper.succeed()
        }
    }

    @GameTest(template = "empty")
    fun nativeChunkClearDoesNotDestroyObserver(helper: GameTestHelper) = helper.immediate {
        // Exercise the native unload cleanup on a task-owned detached chunk, not
        // the shared harness chunk containing other tests.
        val pos = BlockPos(16000, 64, 16000)
        val chunk = LevelChunk(helper.level, ChunkPos(pos.x shr 4, pos.z shr 4))
        val entity = ObservedEntity(pos, NativeFixtures.block.get().defaultBlockState())
        chunk.setBlockState(pos, entity.blockState, 0)
        chunk.setBlockEntity(entity)
        chunk.clearAllBlockEntities()
        check(entity.isRemoved && entity.destroys == 0)
        check(entity.cargo.getItem(0).count == 4)
    }

    @GameTest(template = "empty")
    fun nativeSaveRetainsRootPayloadAndRegistryType(helper: GameTestHelper) = helper.immediate {
        val pos = helper.absolutePos(BlockPos(1, 1, 1))
        val state = NativeFixtures.block.get().defaultBlockState()
        helper.level.setBlockAndUpdate(pos, state)
        val entity = helper.level.getBlockEntity(pos) as ObservedEntity
        entity.payload = 731
        val saved = entity.saveWithFullMetadata(helper.level.registryAccess())
        check(saved.getIntOr("fixture_payload", -1) == 731)
        check(saved.getStringOr("id", "") == "broccolium_testmod:observer")
        check(!saved.contains("data"))
        val reloaded = BlockEntity.loadStatic(pos, state, saved, helper.level.registryAccess()) as ObservedEntity
        check(reloaded.payload == 731)
        check(entity.getUpdateTag(helper.level.registryAccess()).getIntOr("fixture_payload", -1) == 731)
        check(BuiltInRegistries.BLOCK.getKey(state.block).toString() == "broccolium_testmod:observer")
        check(BuiltInRegistries.ITEM.getKey(state.block.asItem()).toString() == "broccolium_testmod:observer")
    }

    @GameTest(template = "empty")
    fun horseCargoExcludesEquipmentAndKeepsEveryCargoSlot(helper: GameTestHelper) = helper.immediate {
        val horse = checkNotNull(EntityType.DONKEY.create(helper.level, EntitySpawnReason.COMMAND))
        check(checkNotNull(horse.getSlot(499)).set(ItemStack(Items.CHEST)))
        horse.setItemSlot(EquipmentSlot.SADDLE, ItemStack(Items.SADDLE))
        horse.setPos(helper.absolutePos(BlockPos(1, 1, 1)).center)
        helper.level.addFreshEntity(horse)
        try {
            val storage = checkNotNull(AgnosticItemStorageLookup.extractFromEntity(helper.level, horse, null))
            horse.inventory.setItem(0, ItemStack(Items.DIAMOND, 3))
            horse.inventory.setItem(horse.inventory.containerSize - 1, ItemStack(Items.EMERALD, 5))
            check(storage.getContent().asSequence().sumOf { it.count } == 8)
            check(storage.take({ it.`is`(Items.DIAMOND) }, 10, false).count == 3)
            check(storage.take({ it.`is`(Items.EMERALD) }, 10, false).count == 5)
            check(horse.getItemBySlot(EquipmentSlot.SADDLE).`is`(Items.SADDLE))
        } finally {
            horse.discard()
        }
    }

    @GameTest(template = "empty")
    fun removedWorldChestIsNotExposed(helper: GameTestHelper) = helper.immediate {
        val pos = helper.absolutePos(BlockPos(1, 1, 1))
        helper.level.setBlockAndUpdate(pos, Blocks.CHEST.defaultBlockState())
        val entity = checkNotNull(helper.level.getBlockEntity(pos))
        helper.level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState())
        check(entity.isRemoved)
        check(AgnosticItemStorageLookup.extractFromBlock(helper.level, pos, entity, null) == null) { "Removed entities must not expose stale inventories" }
    }

    @GameTest(template = "empty")
    fun worldChestLookupConservesItems(helper: GameTestHelper) = helper.immediate {
        val pos = helper.absolutePos(BlockPos(1, 1, 1))
        helper.level.setBlockAndUpdate(pos, Blocks.CHEST.defaultBlockState())
        val entity = checkNotNull(helper.level.getBlockEntity(pos))
        val storage = checkNotNull(AgnosticItemStorageLookup.extractFromBlock(helper.level, pos, entity, null))
        check(storage.store(ItemStack(Items.DIAMOND, 5), false).isEmpty)
        check(storage.take({ it.`is`(Items.DIAMOND) }, 3, false).count == 3)
        check(storage.getContent().asSequence().sumOf { it.count } == 2)
    }
}
