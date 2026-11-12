package site.siredvin.tweakium.testmod

import dan200.computercraft.api.peripheral.IPeripheral
import dan200.computercraft.api.pocket.IPocketAccess
import dan200.computercraft.api.pocket.IPocketUpgrade
import dan200.computercraft.api.turtle.ITurtleAccess
import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.api.turtle.TurtleSide
import dan200.computercraft.api.turtle.TurtleUpgradeType
import dan200.computercraft.api.upgrades.UpgradeData
import dan200.computercraft.shared.ModRegistry
import dan200.computercraft.shared.pocket.items.PocketComputerItem
import dan200.computercraft.shared.turtle.blocks.TurtleBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.gametest.framework.GameTest
import net.minecraft.gametest.framework.GameTestHelper
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import site.siredvin.testiarium.api.TestGroup
import site.siredvin.testiarium.api.immediate
import site.siredvin.tweakium.modules.peripheral.api.IOwnedPeripheral
import site.siredvin.tweakium.modules.peripheral.util.DataStorageUtil
import site.siredvin.tweakium.modules.pocket.StatefulPocketUpgrade
import site.siredvin.tweakium.modules.turtle.StatefulTurtleUpgrade

// These tests exercise the item-less upgrade API that CC:Tweaked 1.121 deprecates but Tweakium still builds on.
@Suppress("DEPRECATION")
@TestGroup("tweakium")
class UpgradeBehaviorGameTests {
    @GameTest(template = "modulebehavior.empty")
    fun pocketStateSurvivesItemSerializationWithoutChangingCraftingItem(helper: GameTestHelper) = helper.immediate {
        val upgrade = object : StatefulPocketUpgrade<IOwnedPeripheral<*>>(ResourceLocation("tweakium", "test_pocket"), ItemStack(Items.DIAMOND)) {
            override fun getPeripheral(access: IPocketAccess): IOwnedPeripheral<*> = error("No peripheral construction needed for item persistence")
        }
        roundTrip(upgrade::getUpgradeItem, upgrade::getUpgradeData, upgrade::isItemSuitable)
        check(upgrade.craftingItem.tag == null)
    }

    @GameTest(template = "modulebehavior.empty")
    fun turtleStateSurvivesItemSerializationWithoutChangingCraftingItem(helper: GameTestHelper) = helper.immediate {
        val upgrade = object : StatefulTurtleUpgrade<IOwnedPeripheral<*>>(ResourceLocation("tweakium", "test_turtle"), TurtleUpgradeType.PERIPHERAL, ItemStack(Items.DIAMOND)) {
            override fun buildPeripheral(turtle: ITurtleAccess, side: TurtleSide): IOwnedPeripheral<*> = error("No peripheral construction needed for item persistence")
        }
        roundTrip(upgrade::getUpgradeItem, upgrade::getUpgradeData, upgrade::isItemSuitable)
        check(upgrade.craftingItem.tag == null)
    }

    @GameTest(template = "modulebehavior.empty")
    fun turtleKeepsEquippedItemThroughDataWrites(helper: GameTestHelper) = helper.immediate {
        val upgrade = statefulTurtleUpgrade()
        val entity = placeTurtle(helper)
        val turtle = entity.access
        val equipped = upgrade.getUpgradeItem(state())
        // The upgrade data CC builds when a turtle equips a stack.
        turtle.setUpgradeWithData(TurtleSide.LEFT, UpgradeData.of<ITurtleUpgrade>(upgrade, equipped, upgrade.getUpgradeData(equipped)))
        try {
            check(ItemStack.matches(turtle.getUpgradeHandle(TurtleSide.LEFT)!!.item, equipped)) { "Equipping changed the equipped item" }
            DataStorageUtil.getDataStorage(turtle, TurtleSide.LEFT).putString("owner", "birch")
            val expected = equipped.copy().apply { getTagElement(StatefulTurtleUpgrade.STORED_DATA_TAG)!!.putString("owner", "birch") }
            check(ItemStack.matches(turtle.getUpgradeWithData(TurtleSide.LEFT)!!.upgradeItem, expected)) { "A data write lost the equipped item or its new state" }
            check(ItemStack.matches(ItemStack.of(entity.saveWithoutMetadata().getCompound("LeftUpgradeItem")), expected)) { "The equipped item was not saved" }
        } finally {
            turtle.setUpgradeWithData(TurtleSide.LEFT, null)
        }
    }

    @GameTest(template = "modulebehavior.empty")
    fun pocketKeepsEquippedItemInSavedComputer(helper: GameTestHelper) = helper.immediate {
        val upgrade = statefulPocketUpgrade()
        val equipped = upgrade.getUpgradeItem(state())
        val data = UpgradeData.of<IPocketUpgrade>(upgrade, equipped, upgrade.getUpgradeData(equipped))
        check(ItemStack.matches(data.upgradeItem, equipped)) { "Equipping changed the equipped item" }
        val computer = ItemStack(ModRegistry.Items.POCKET_COMPUTER_NORMAL.get())
        PocketComputerItem.setUpgrade(computer, data)
        check(ItemStack.matches(ItemStack.of(computer.tag!!.getCompound("UpgradeItem")), equipped)) { "The equipped item was not saved" }
        check(computer.tag!!.getCompound("UpgradeInfo") == state())
    }

    @GameTest(template = "modulebehavior.empty")
    fun upgradeDataSavedWithoutItemStillLoads(helper: GameTestHelper) = helper.immediate {
        // The disk format before CC:Tweaked 1.121 (libs 2.0.x): an upgrade id and its data, no item.
        val speaker = ModRegistry.Items.SPEAKER.get()
        val entity = placeTurtle(helper)
        entity.load(
            entity.saveWithoutMetadata().apply {
                putString("LeftUpgrade", "computercraft:speaker")
                put("LeftUpgradeNbt", state())
            },
        )
        val left = entity.access.getUpgradeWithData(TurtleSide.LEFT) ?: error("The item-less turtle upgrade was dropped")
        check(left.data() == state() && left.upgradeItem.`is`(speaker))
        val saved = entity.saveWithoutMetadata()
        check(saved.getCompound("LeftUpgradeNbt") == state() && ItemStack.of(saved.getCompound("LeftUpgradeItem")).`is`(speaker))

        val computer = ItemStack(ModRegistry.Items.POCKET_COMPUTER_NORMAL.get()).apply {
            orCreateTag.putString("Upgrade", "computercraft:speaker")
            orCreateTag.put("UpgradeInfo", state())
        }
        val back = PocketComputerItem.getUpgradeWithData(computer) ?: error("The item-less pocket upgrade was dropped")
        check(back.data() == state() && back.upgradeItem.`is`(speaker))

        // A stateful upgrade rebuilds its item from the old data, and CC accepts the rebuilt item.
        val upgrade = statefulTurtleUpgrade()
        entity.access.setUpgradeWithData(TurtleSide.RIGHT, UpgradeData.of<ITurtleUpgrade>(upgrade, state()))
        try {
            check(ItemStack.matches(entity.access.getUpgradeWithData(TurtleSide.RIGHT)!!.upgradeItem, upgrade.getUpgradeItem(state())))
        } finally {
            entity.access.setUpgradeWithData(TurtleSide.RIGHT, null)
        }
    }

    private fun state() = CompoundTag().apply {
        putInt("fuel", 42)
        putString("owner", "oak")
    }

    private fun placeTurtle(helper: GameTestHelper): TurtleBlockEntity {
        val pos = BlockPos(1, 1, 1)
        helper.setBlock(pos, ModRegistry.Blocks.TURTLE_NORMAL.get())
        return helper.level.getBlockEntity(helper.absolutePos(pos)) as TurtleBlockEntity
    }

    private fun statefulTurtleUpgrade() = object : StatefulTurtleUpgrade<IOwnedPeripheral<*>>(ResourceLocation("tweakium", "test_turtle"), TurtleUpgradeType.PERIPHERAL, ItemStack(Items.DIAMOND)) {
        override fun buildPeripheral(turtle: ITurtleAccess, side: TurtleSide): IOwnedPeripheral<*> = error("No peripheral construction needed for item persistence")
        override fun createPeripheral(turtle: ITurtleAccess, side: TurtleSide): IPeripheral? = null
    }

    private fun statefulPocketUpgrade() = object : StatefulPocketUpgrade<IOwnedPeripheral<*>>(ResourceLocation("tweakium", "test_pocket"), ItemStack(Items.DIAMOND)) {
        override fun getPeripheral(access: IPocketAccess): IOwnedPeripheral<*> = error("No peripheral construction needed for item persistence")
    }

    private fun roundTrip(write: (CompoundTag) -> ItemStack, read: (ItemStack) -> CompoundTag, suitable: (ItemStack) -> Boolean) {
        val data = CompoundTag().apply {
            putInt("fuel", 42)
            putString("owner", "oak")
        }
        val item = write(data)
        check(suitable(item))
        val unrelatedData = item.copy().apply { orCreateTag.putString("unrelated", "must not be lost") }
        check(!suitable(unrelatedData))
        val restored = ItemStack.of(item.save(CompoundTag()))
        check(read(restored) == data)
        check(read(write(CompoundTag())).isEmpty)
        check(read(item).getInt("fuel") == 42)
    }
}
