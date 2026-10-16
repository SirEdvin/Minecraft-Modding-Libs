package site.siredvin.tweakium.testmod

import dan200.computercraft.api.pocket.IPocketAccess
import dan200.computercraft.api.turtle.ITurtleAccess
import dan200.computercraft.api.turtle.TurtleSide
import dan200.computercraft.api.turtle.TurtleUpgradeType
import net.minecraft.gametest.framework.GameTest
import net.minecraft.gametest.framework.GameTestHelper
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import site.siredvin.testiarium.api.TestGroup
import site.siredvin.testiarium.api.immediate
import site.siredvin.tweakium.modules.peripheral.api.IOwnedPeripheral
import site.siredvin.tweakium.modules.pocket.StatefulPocketUpgrade
import site.siredvin.tweakium.modules.turtle.StatefulTurtleUpgrade

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
