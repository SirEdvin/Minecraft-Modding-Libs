package site.siredvin.tweakium.testmod

import dan200.computercraft.api.pocket.IPocketAccess
import dan200.computercraft.api.pocket.IPocketUpgrade
import dan200.computercraft.api.turtle.ITurtleAccess
import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.api.turtle.TurtleSide
import dan200.computercraft.api.turtle.TurtleUpgradeType
import dan200.computercraft.api.upgrades.UpgradeType
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.component.DataComponents
import net.minecraft.gametest.framework.GameTest
import net.minecraft.gametest.framework.GameTestHelper
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.CustomData
import site.siredvin.testiarium.api.TestGroup
import site.siredvin.testiarium.api.immediate
import site.siredvin.tweakium.modules.peripheral.api.IOwnedPeripheral
import site.siredvin.tweakium.modules.pocket.StatefulPocketUpgrade
import site.siredvin.tweakium.modules.turtle.StatefulTurtleUpgrade

@TestGroup("tweakium")
class UpgradeBehaviorGameTests {
    @GameTest(template = "modulebehavior.empty")
    fun pocketStateSurvivesItemSerializationWithoutChangingCraftingItem(helper: GameTestHelper) = helper.immediate {
        val upgrade = object : StatefulPocketUpgrade<IOwnedPeripheral<*>>(ResourceLocation.fromNamespaceAndPath("tweakium", "test_pocket"), ItemStack(Items.DIAMOND)) {
            override fun getPeripheral(access: IPocketAccess): IOwnedPeripheral<*> = error("No peripheral construction needed for item persistence")
            override fun getType(): UpgradeType<out IPocketUpgrade> = error("No registration needed for item persistence")
        }
        roundTrip(helper, upgrade::getUpgradeItem, upgrade::getUpgradeData, upgrade::isItemSuitable)
        check(!upgrade.craftingItem.has(DataComponents.CUSTOM_DATA))
    }

    @GameTest(template = "modulebehavior.empty")
    fun turtleStateSurvivesItemSerializationWithoutChangingCraftingItem(helper: GameTestHelper) = helper.immediate {
        val upgrade = object : StatefulTurtleUpgrade<IOwnedPeripheral<*>>(ResourceLocation.fromNamespaceAndPath("tweakium", "test_turtle"), TurtleUpgradeType.PERIPHERAL, ItemStack(Items.DIAMOND)) {
            override fun buildPeripheral(turtle: ITurtleAccess, side: TurtleSide): IOwnedPeripheral<*> = error("No peripheral construction needed for item persistence")
            override fun getType(): UpgradeType<out ITurtleUpgrade> = error("No registration needed for item persistence")
        }
        roundTrip(helper, upgrade::getUpgradeItem, upgrade::getUpgradeData, upgrade::isItemSuitable)
        check(!upgrade.craftingItem.has(DataComponents.CUSTOM_DATA))
    }

    @GameTest(template = "modulebehavior.empty")
    fun pocketExplicitRemovalSurvivesItemRoundTrip(helper: GameTestHelper) = helper.immediate {
        val crafting = ItemStack(Items.DIAMOND).apply {
            set(DataComponents.CUSTOM_DATA, CustomData.of(CompoundTag().apply { putString("default", "must stay removed") }))
        }
        val upgrade = object : StatefulPocketUpgrade<IOwnedPeripheral<*>>(ResourceLocation.fromNamespaceAndPath("tweakium", "test_removal"), crafting) {
            override fun getPeripheral(access: IPocketAccess): IOwnedPeripheral<*> = error("Item conversion only")
            override fun getType(): UpgradeType<out IPocketUpgrade> = error("Item conversion only")
        }
        removalRoundTrip(helper, upgrade.craftingItem, upgrade::getUpgradeItem, upgrade::getUpgradeData)
    }

    @GameTest(template = "modulebehavior.empty")
    fun turtleExplicitRemovalSurvivesItemRoundTrip(helper: GameTestHelper) = helper.immediate {
        val crafting = ItemStack(Items.DIAMOND).apply {
            set(DataComponents.CUSTOM_DATA, CustomData.of(CompoundTag().apply { putString("default", "must stay removed") }))
        }
        val upgrade = object : StatefulTurtleUpgrade<IOwnedPeripheral<*>>(ResourceLocation.fromNamespaceAndPath("tweakium", "test_removal"), TurtleUpgradeType.PERIPHERAL, crafting) {
            override fun buildPeripheral(turtle: ITurtleAccess, side: TurtleSide): IOwnedPeripheral<*> = error("Item conversion only")
            override fun getType(): UpgradeType<out ITurtleUpgrade> = error("Item conversion only")
        }
        removalRoundTrip(helper, upgrade.craftingItem, upgrade::getUpgradeItem, upgrade::getUpgradeData)
    }

    private fun removalRoundTrip(helper: GameTestHelper, crafting: ItemStack, write: (DataComponentPatch) -> ItemStack, read: (ItemStack) -> DataComponentPatch) {
        check(write(DataComponentPatch.EMPTY).has(DataComponents.CUSTOM_DATA)) { "Unspecified components must retain crafting defaults" }
        val removal = DataComponentPatch.builder().remove(DataComponents.CUSTOM_DATA).build()
        val withoutData = write(removal)
        check(!withoutData.has(DataComponents.CUSTOM_DATA)) { "Explicit removal restored the crafting default" }
        check(crafting.has(DataComponents.CUSTOM_DATA)) { "Converting upgrade data changed the crafting template" }
        val restored = ItemStack.parseOptional(helper.level.registryAccess(), withoutData.save(helper.level.registryAccess()) as CompoundTag)
        val readBack = read(restored)
        check(readBack.get(DataComponents.CUSTOM_DATA)?.isEmpty == true) { "Item conversion lost the removal relative to the crafting template" }
        check(!write(readBack).has(DataComponents.CUSTOM_DATA))
    }

    private fun roundTrip(helper: GameTestHelper, write: (DataComponentPatch) -> ItemStack, read: (ItemStack) -> DataComponentPatch, suitable: (ItemStack) -> Boolean) {
        val data = CompoundTag().apply {
            putInt("fuel", 42)
            putString("owner", "oak")
        }
        val patch = DataComponentPatch.builder().set(DataComponents.CUSTOM_DATA, CustomData.of(data)).build()
        val item = write(patch)
        check(suitable(item))
        val unrelatedData = item.copy().apply { set(DataComponents.CUSTOM_NAME, Component.literal("must not be lost")) }
        check(!suitable(unrelatedData))
        val restored = ItemStack.parseOptional(helper.level.registryAccess(), item.save(helper.level.registryAccess()) as CompoundTag)
        check(read(restored) == patch)
        check(read(write(DataComponentPatch.EMPTY)).isEmpty)
        check(item.get(DataComponents.CUSTOM_DATA)!!.copyTag().getInt("fuel") == 42)
    }
}
