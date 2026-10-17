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
import net.minecraft.gametest.framework.GameTestHelper
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.ItemStackTemplate
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.CustomData
import site.siredvin.testiarium.api.GameTest
import site.siredvin.testiarium.api.TestGroup
import site.siredvin.testiarium.api.immediate
import site.siredvin.tweakium.modules.peripheral.api.IOwnedPeripheral
import site.siredvin.tweakium.modules.pocket.StatefulPocketUpgrade
import site.siredvin.tweakium.modules.turtle.StatefulTurtleUpgrade

@TestGroup("tweakium")
class UpgradeBehaviorGameTests {
    private fun pocket(crafting: ItemStackTemplate) = object : StatefulPocketUpgrade<IOwnedPeripheral<*>>(Identifier.fromNamespaceAndPath("tweakium", "test_pocket"), crafting) {
        override fun getPeripheral(access: IPocketAccess): IOwnedPeripheral<*> = error("Item conversion only")
        override fun getType(): UpgradeType<out IPocketUpgrade> = error("Item conversion only")
    }

    private fun turtle(crafting: ItemStackTemplate) = object : StatefulTurtleUpgrade<IOwnedPeripheral<*>>(Identifier.fromNamespaceAndPath("tweakium", "test_turtle"), TurtleUpgradeType.PERIPHERAL, crafting) {
        override fun buildPeripheral(turtle: ITurtleAccess, side: TurtleSide): IOwnedPeripheral<*> = error("Item conversion only")
        override fun getType(): UpgradeType<out ITurtleUpgrade> = error("Item conversion only")
    }

    @GameTest(template = "modulebehavior.empty")
    fun pocketStateSurvivesItemSerializationWithoutChangingCraftingItem(helper: GameTestHelper) = helper.immediate {
        val upgrade = pocket(ItemStackTemplate.fromNonEmptyStack(ItemStack(Items.DIAMOND)))
        roundTrip(helper, upgrade::getUpgradeItem, upgrade::getUpgradeData, upgrade::isItemSuitable)
        check(!upgrade.craftingItem.create().has(DataComponents.CUSTOM_DATA))
    }

    @GameTest(template = "modulebehavior.empty")
    fun turtleStateSurvivesItemSerializationWithoutChangingCraftingItem(helper: GameTestHelper) = helper.immediate {
        val upgrade = turtle(ItemStackTemplate.fromNonEmptyStack(ItemStack(Items.DIAMOND)))
        roundTrip(helper, upgrade::getUpgradeItem, upgrade::getUpgradeData, upgrade::isItemSuitable)
        check(!upgrade.craftingItem.create().has(DataComponents.CUSTOM_DATA))
    }

    @GameTest(template = "modulebehavior.empty")
    fun pocketExplicitRemovalSurvivesItemRoundTrip(helper: GameTestHelper) = helper.immediate {
        val upgrade = pocket(defaults())
        removalRoundTrip(helper, upgrade.craftingItem, upgrade::getUpgradeItem, upgrade::getUpgradeData)
    }

    @GameTest(template = "modulebehavior.empty")
    fun turtleExplicitRemovalSurvivesItemRoundTrip(helper: GameTestHelper) = helper.immediate {
        val upgrade = turtle(defaults())
        removalRoundTrip(helper, upgrade.craftingItem, upgrade::getUpgradeItem, upgrade::getUpgradeData)
    }

    private fun defaults(): ItemStackTemplate = ItemStackTemplate.fromNonEmptyStack(ItemStack(Items.DIAMOND).apply {
        set(DataComponents.CUSTOM_DATA, CustomData.of(CompoundTag().apply { putString("default", "must stay removed") }))
    })

    private fun persisted(helper: GameTestHelper, item: ItemStack): ItemStack {
        val ops = helper.level.registryAccess().createSerializationContext(NbtOps.INSTANCE)
        return ItemStack.CODEC.parse(ops, ItemStack.CODEC.encodeStart(ops, item).orThrow).orThrow
    }

    private fun removalRoundTrip(helper: GameTestHelper, crafting: ItemStackTemplate, write: (DataComponentPatch) -> ItemStackTemplate, read: (ItemStackTemplate) -> DataComponentPatch) {
        check(write(DataComponentPatch.EMPTY).create().has(DataComponents.CUSTOM_DATA))
        val withoutData = write(DataComponentPatch.builder().remove(DataComponents.CUSTOM_DATA).build())
        check(!withoutData.create().has(DataComponents.CUSTOM_DATA)) { "Explicit removal restored the crafting default" }
        check(crafting.create().has(DataComponents.CUSTOM_DATA)) { "Converting upgrade data changed the crafting template" }
        val restored = ItemStackTemplate.fromNonEmptyStack(persisted(helper, withoutData.create()))
        val readBack = read(restored)
        check(readBack.entrySet().first { it.key == DataComponents.CUSTOM_DATA }.value.isEmpty)
        check(!write(readBack).create().has(DataComponents.CUSTOM_DATA))
    }

    private fun roundTrip(helper: GameTestHelper, write: (DataComponentPatch) -> ItemStackTemplate, read: (ItemStackTemplate) -> DataComponentPatch, suitable: (ItemStack) -> Boolean) {
        val patch = DataComponentPatch.builder().set(DataComponents.CUSTOM_DATA, CustomData.of(CompoundTag().apply {
            putInt("fuel", 42)
            putString("owner", "oak")
        })).build()
        val item = write(patch).create()
        check(suitable(item))
        check(!suitable(item.copy().apply { set(DataComponents.CUSTOM_NAME, Component.literal("must not be lost")) }))
        check(read(ItemStackTemplate.fromNonEmptyStack(persisted(helper, item))) == patch)
        check(read(write(DataComponentPatch.EMPTY)).isEmpty)
        check(item.get(DataComponents.CUSTOM_DATA)!!.copyTag().getIntOr("fuel", 0) == 42)
    }
}
