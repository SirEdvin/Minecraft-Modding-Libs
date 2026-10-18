package site.siredvin.tweakium.testmod

import dan200.computercraft.api.pocket.IPocketUpgrade
import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.api.turtle.TurtleSide
import dan200.computercraft.api.upgrades.UpgradeData
import dan200.computercraft.shared.ModRegistry
import dan200.computercraft.shared.pocket.core.PocketHolder
import dan200.computercraft.shared.pocket.core.PocketSide
import dan200.computercraft.shared.pocket.items.PocketComputerItem
import dan200.computercraft.shared.turtle.blocks.TurtleBlockEntity
import dan200.computercraft.shared.turtle.core.TurtleBrain
import net.minecraft.core.BlockPos
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.component.DataComponents
import net.minecraft.gametest.framework.GameTestHelper
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.ItemStackTemplate
import net.minecraft.world.item.Items
import net.minecraft.world.level.ChunkPos
import site.siredvin.testiarium.api.GameTest
import site.siredvin.testiarium.api.TestGroup
import site.siredvin.testiarium.api.immediate
import site.siredvin.tweakium.modules.peripheral.api.IOwnedPeripheral
import site.siredvin.tweakium.modules.peripheral.util.CreativeTabUtil
import site.siredvin.tweakium.modules.peripheral.util.DataStorageUtil
import site.siredvin.tweakium.modules.platform.ComputerPlatformToolkit
import site.siredvin.tweakium.modules.player.FakePlayerProviderPocket

@TestGroup("tweakium")
class NativeUpgradeGameTests {
    @GameTest(template = "modulebehavior.empty")
    fun creativeTabResolvesOnlyOwningRegisteredUpgradesAndIndependentStacks(helper: GameTestHelper) = helper.immediate {
        check(java.lang.reflect.Modifier.isPublic(CreativeModeTab.Output::class.java.modifiers))
        check(java.lang.reflect.Modifier.isPublic(net.minecraft.world.entity.animal.equine.AbstractHorse::class.java.getDeclaredField("inventory").modifiers))
        val stacks = mutableListOf<ItemStack>()
        val output = object : CreativeModeTab.Output {
            override fun accept(stack: ItemStack, visibility: CreativeModeTab.TabVisibility) {
                stacks.add(stack)
            }
        }
        CreativeTabUtil.enrichCreativeTabWithUpgrades("tweakium", output, helper.level.registryAccess())
        check(stacks.size == 8)
        val upgraded = stacks.filter { it.has(ModRegistry.DataComponents.RIGHT_TURTLE_UPGRADE.get()) || it.has(ModRegistry.DataComponents.BACK_POCKET_UPGRADE.get()) }
        check(upgraded.size == 4)
        for (stack in upgraded) {
            val upgrade = stack.get(ModRegistry.DataComponents.RIGHT_TURTLE_UPGRADE.get())?.holder()
                ?: stack.get(ModRegistry.DataComponents.BACK_POCKET_UPGRADE.get())!!.holder()
            check(upgrade.key().identifier() == NativeUpgradeFixtures.ID)
        }
        upgraded.first().count = 2
        check(upgraded.drop(1).all { it.count == 1 })
        stacks.clear()
        CreativeTabUtil.enrichCreativeTabWithUpgrades("no_matching_fixture", output, helper.level.registryAccess())
        check(stacks.size == 4)
        check(stacks.none { it.has(ModRegistry.DataComponents.RIGHT_TURTLE_UPGRADE.get()) || it.has(ModRegistry.DataComponents.BACK_POCKET_UPGRADE.get()) })
    }

    private fun turtle(helper: GameTestHelper, pos: BlockPos): TurtleBrain {
        helper.level.setBlockAndUpdate(pos, ModRegistry.Blocks.TURTLE_NORMAL.get().defaultBlockState())
        return (helper.level.getBlockEntity(pos) as TurtleBlockEntity).access as TurtleBrain
    }

    @GameTest(template = "modulebehavior.empty")
    fun liveTurtleInstallRemoveReinstallKeepsIndependentSideSettings(helper: GameTestHelper) = helper.immediate {
        val turtle = turtle(helper, helper.absolutePos(BlockPos(1, 1, 1)))
        val holder = helper.level.registryAccess().lookupOrThrow(ITurtleUpgrade.REGISTRY).getOrThrow(net.minecraft.resources.ResourceKey.create(ITurtleUpgrade.REGISTRY, NativeUpgradeFixtures.ID))
        for (side in TurtleSide.entries) {
            turtle.setUpgrade(side, UpgradeData.ofDefault(holder))
            check(turtle.getPeripheral(side) is IOwnedPeripheral<*>)
            val storage = DataStorageUtil.getDataStorage(turtle, side)
            storage.putString("ownerProfile", "owner-$side")
            storage.putInt("fuel", side.ordinal + 10)
        }
        for (side in TurtleSide.entries) {
            val before = turtle.getUpgradeWithData(side)!!
            val removed = before.upgradeItem
            val item = removed.create()
            check(ComputerPlatformToolkit.get().getTurtleUpgrade(helper.level.registryAccess(), item) == before)
            turtle.setUpgrade(side, null)
            check(turtle.getPeripheral(side) == null)
            turtle.setUpgrade(side, UpgradeData.of(holder, holder.value().getUpgradeData(ItemStackTemplate.fromNonEmptyStack(item))))
            val storage = DataStorageUtil.getDataStorage(turtle, side)
            check(storage.getString("ownerProfile") == "owner-$side" && storage.getInt("fuel") == side.ordinal + 10)
        }
    }

    @GameTest(template = "modulebehavior.empty")
    fun livePocketInstallRemoveReinstallUsesNativeSideAccessAndPlayerInventory(helper: GameTestHelper) = helper.immediate {
        @Suppress("DEPRECATION")
        val player = helper.makeMockServerPlayerInLevel()
        NativeUpgradeFixtures.configurePlayer.accept(player)
        val item = ModRegistry.Items.POCKET_COMPUTER_NORMAL.get() as PocketComputerItem
        val stack = ItemStack(item)
        player.inventory.setItem(0, stack)
        val holder = helper.level.registryAccess().lookupOrThrow(IPocketUpgrade.REGISTRY).getOrThrow(net.minecraft.resources.ResourceKey.create(IPocketUpgrade.REGISTRY, NativeUpgradeFixtures.ID))
        try {
            val brain = item.getOrCreateBrain(PocketHolder.PlayerHolder(player, 0), stack)
            for (side in PocketSide.entries) {
                brain.setUpgrade(side, UpgradeData.ofDefault(holder))
                val upgrade = holder.value() as NativeUpgradeFixtures.Pocket
                val access = checkNotNull(upgrade.access)
                check(access.entity === player)
                val storage = DataStorageUtil.getDataStorage(access)
                storage.patch = DataComponentPatch.builder().remove(DataComponents.CUSTOM_NAME).set(DataComponents.DAMAGE, 5).build()
                storage.putString("ownerProfile", "pocket-$side")
                val removed = brain.getUpgrade(side)!!
                brain.setUpgrade(side, null)
                brain.setUpgrade(side, removed)
                check(DataStorageUtil.getDataStorage(checkNotNull(upgrade.access)).getString("ownerProfile") == "pocket-$side")
                check(brain.getUpgrade(side)!!.data().entrySet().first { it.key == DataComponents.CUSTOM_NAME }.value.isEmpty)
                player.inventory.setItem(8, ItemStack(Items.DIAMOND, 4))
                player.inventory.selectedSlot = 8
                FakePlayerProviderPocket.withPlayer(checkNotNull(upgrade.access), { proxy ->
                    check(proxy.fakePlayer.mainHandItem.`is`(Items.DIAMOND))
                    proxy.fakePlayer.mainHandItem.shrink(1)
                    player.inventory.selectedSlot = 7
                })
                check(player.inventory.getItem(8).count == 3 && player.inventory.getItem(7).isEmpty)
            }
        } finally {
            player.inventory.clearContent()
            helper.level.server.playerList.remove(player)
            player.connection.disconnect(net.minecraft.network.chat.Component.literal("R3 fixture complete"))
            player.discard()
        }
    }

    @GameTest(template = "modulebehavior.empty", timeoutTicks = 600)
    fun nativeChunkDiskUnloadReloadKeepsInstalledUpgradeSettings(helper: GameTestHelper) {
        val pos = helper.absolutePos(BlockPos(1, 1, 1)).offset(2048, 0, 2048)
        val chunk = ChunkPos(pos.x shr 4, pos.z shr 4)
        helper.runBeforeTestEnd {
            helper.level.setChunkForced(chunk.x, chunk.z, false)
            if (helper.level.chunkSource.getChunkNow(chunk.x, chunk.z) != null) helper.level.setBlockAndUpdate(pos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState())
        }
        helper.level.setChunkForced(chunk.x, chunk.z, true)
        val turtle = turtle(helper, pos)
        val holder = helper.level.registryAccess().lookupOrThrow(ITurtleUpgrade.REGISTRY).getOrThrow(net.minecraft.resources.ResourceKey.create(ITurtleUpgrade.REGISTRY, NativeUpgradeFixtures.ID))
        turtle.setUpgrade(TurtleSide.LEFT, UpgradeData.ofDefault(holder))
        DataStorageUtil.getDataStorage(turtle, TurtleSide.LEFT).putString("ownerProfile", "disk-owner")
        DataStorageUtil.getDataStorage(turtle, TurtleSide.LEFT).putInt("fuel", 81)
        helper.level.chunkSource.save(false)
        helper.level.setChunkForced(chunk.x, chunk.z, false)
        helper.runAfterDelay(100) {
            check(helper.level.chunkSource.getChunkNow(chunk.x, chunk.z) == null) { "Task-owned distant chunk did not unload" }
            helper.level.getChunk(chunk.x, chunk.z)
            val restored = (helper.level.getBlockEntity(pos) as TurtleBlockEntity).access
            check(restored !== turtle) { "Disk reload reused unloaded turtle brain" }
            check(DataStorageUtil.getDataStorage(restored, TurtleSide.LEFT).getString("ownerProfile") == "disk-owner") { "Disk reload lost upgrade ownerProfile (upgrade=${restored.getUpgrade(TurtleSide.LEFT)})" }
            check(DataStorageUtil.getDataStorage(restored, TurtleSide.LEFT).getInt("fuel") == 81) { "Disk reload lost upgrade fuel" }
            helper.level.setBlockAndUpdate(pos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState())
            helper.succeed()
        }
    }
}
