package site.siredvin.tweakium.testmod

import com.mojang.serialization.Dynamic
import com.mojang.serialization.Lifecycle
import dan200.computercraft.api.peripheral.IPeripheral
import dan200.computercraft.api.pocket.IPocketAccess
import dan200.computercraft.api.pocket.IPocketUpgrade
import dan200.computercraft.api.turtle.ITurtleAccess
import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.api.turtle.TurtleSide
import dan200.computercraft.api.turtle.TurtleUpgradeType
import dan200.computercraft.api.upgrades.UpgradeBase
import dan200.computercraft.api.upgrades.UpgradeData
import dan200.computercraft.api.upgrades.UpgradeType
import dan200.computercraft.shared.ModRegistry
import dan200.computercraft.shared.pocket.items.PocketComputerItem
import dan200.computercraft.shared.turtle.blocks.TurtleBlockEntity
import net.minecraft.SharedConstants
import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.core.MappedRegistry
import net.minecraft.core.RegistrationInfo
import net.minecraft.core.Registry
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.component.DataComponents
import net.minecraft.gametest.framework.GameTest
import net.minecraft.gametest.framework.GameTestHelper
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.TagParser
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.datafix.DataFixers
import net.minecraft.util.datafix.fixes.References
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.CustomData
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

    @GameTest(template = "modulebehavior.empty")
    fun turtleKeepsEquippedItemThroughDataWrites(helper: GameTestHelper) = helper.immediate {
        val upgrade = statefulTurtleUpgrade()
        val holder = holder(ITurtleUpgrade.REGISTRY, upgrade)
        val turtle = placeTurtle(helper).access
        val equipped = upgrade.getUpgradeItem(state("oak"))
        // The upgrade data CC builds when a turtle equips a stack.
        turtle.setUpgrade(TurtleSide.LEFT, UpgradeData.of(holder, equipped, upgrade.getUpgradeData(equipped)))
        try {
            check(ItemStack.matches(turtle.getUpgradeHandle(TurtleSide.LEFT)!!.item, equipped)) { "Equipping changed the equipped item" }
            DataStorageUtil.getDataStorage(turtle, TurtleSide.LEFT).putString("owner", "birch")
            val unequipped = turtle.getUpgradeWithData(TurtleSide.LEFT)!!.upgradeItem
            check(ItemStack.matches(unequipped, upgrade.getUpgradeItem(state("birch")))) { "A data write lost the equipped item or its new state" }
        } finally {
            turtle.setUpgrade(TurtleSide.LEFT, null)
        }
    }

    @GameTest(template = "modulebehavior.empty")
    fun pocketKeepsEquippedItem(helper: GameTestHelper) = helper.immediate {
        val upgrade = statefulPocketUpgrade()
        val holder = holder(IPocketUpgrade.REGISTRY, upgrade)
        val equipped = upgrade.getUpgradeItem(state("oak"))
        check(ItemStack.matches(UpgradeData.of(holder, equipped, upgrade.getUpgradeData(equipped)).upgradeItem, equipped)) { "Equipping changed the equipped item" }
        // Data written before the item was stored rebuilds the same item.
        check(ItemStack.matches(UpgradeData.of(holder, state("oak")).upgradeItem, equipped))
    }

    @GameTest(template = "modulebehavior.empty")
    fun upgradeDataSavedWithoutItemStillLoads(helper: GameTestHelper) = helper.immediate {
        // The disk format before CC:Tweaked 1.121 (libs 2.0.x): an upgrade id and its components, no item.
        val registries = helper.level.registryAccess()
        val speaker = ModRegistry.Items.SPEAKER.get()
        val old = TagParser.parseTag("""{id:"computercraft:speaker",components:{"minecraft:custom_data":{fuel:42,owner:"oak"},"!minecraft:custom_name":{}}}""")
        val entity = placeTurtle(helper)
        entity.loadWithComponents(entity.saveWithoutMetadata(registries).apply { put("LeftUpgrade", old.copy()) }, registries)
        val left = entity.access.getUpgradeWithData(TurtleSide.LEFT) ?: error("The item-less turtle upgrade was dropped")
        check(left.data().get(DataComponents.CUSTOM_DATA)!!.get().copyTag().getString("owner") == "oak")
        check(left.data().get(DataComponents.CUSTOM_NAME)!!.isEmpty && left.upgradeItem.`is`(speaker))
        val saved = entity.saveWithoutMetadata(registries).getCompound("LeftUpgrade")
        check(saved.getCompound("components") == old.getCompound("components") && saved.getCompound("item").getString("id") == "computercraft:speaker")

        val computer = ItemStack.parseOptional(
            registries,
            TagParser.parseTag("""{id:"computercraft:pocket_computer_normal",count:1,components:{"computercraft:pocket_upgrade":$old}}"""),
        )
        val back = PocketComputerItem.getUpgradeWithData(computer) ?: error("The item-less pocket upgrade was dropped")
        check(back.data() == left.data() && back.upgradeItem.`is`(speaker))

        // A stateful upgrade rebuilds its item from the old data, and CC accepts the rebuilt item.
        val upgrade = statefulTurtleUpgrade()
        entity.access.setUpgrade(TurtleSide.RIGHT, UpgradeData.of(holder(ITurtleUpgrade.REGISTRY, upgrade), state("oak")))
        try {
            check(ItemStack.matches(entity.access.getUpgradeWithData(TurtleSide.RIGHT)!!.upgradeItem, upgrade.getUpgradeItem(state("oak"))))
        } finally {
            entity.access.setUpgrade(TurtleSide.RIGHT, null)
        }
    }

    @GameTest(template = "modulebehavior.empty")
    fun upgradeDataFromMinecraft120SurvivesTheDataFixer(helper: GameTestHelper) = helper.immediate {
        val current = SharedConstants.getCurrentVersion().dataVersion.version
        val oldPocket = TagParser.parseTag("""{id:"computercraft:pocket_computer_normal",Count:1b,tag:{Upgrade:"computercraft:speaker",UpgradeInfo:{owner:"back"}}}""")
        val pocket = DataFixers.getDataFixer().update(References.ITEM_STACK, Dynamic(NbtOps.INSTANCE, oldPocket), 3465, current).value as CompoundTag
        val back = PocketComputerItem.getUpgradeWithData(ItemStack.parseOptional(helper.level.registryAccess(), pocket)) ?: error("Pocket upgrade was lost: $pocket")
        check(back.data().get(DataComponents.CUSTOM_DATA)!!.get().copyTag().getString("owner") == "back") { "Pocket upgrade data was lost: $pocket" }

        val old = TagParser.parseTag(
            """{id:"computercraft:turtle_normal",Fuel:1234,Items:[{Slot:0b,id:"minecraft:diamond",Count:11b}],""" +
                """LeftUpgrade:"computercraft:speaker",LeftUpgradeNbt:{owner:"left",storedData:{mode:"retained"}},""" +
                """RightUpgrade:"computercraft:speaker",RightUpgradeNbt:{owner:"right"}}""",
        )
        val fixed = DataFixers.getDataFixer().update(References.BLOCK_ENTITY, Dynamic(NbtOps.INSTANCE, old), 3465, current).value as CompoundTag
        val entity = placeTurtle(helper)
        entity.loadWithComponents(fixed, helper.level.registryAccess())
        for (side in TurtleSide.entries) {
            val data = entity.access.getUpgradeWithData(side)?.data()?.get(DataComponents.CUSTOM_DATA)?.orElse(null)?.copyTag() ?: error("$side upgrade data was lost: $fixed")
            check(data.getString("owner") == side.name.lowercase()) { "$side upgrade data was mixed up: $fixed" }
        }
        check(entity.access.getUpgradeWithData(TurtleSide.LEFT)!!.data().get(DataComponents.CUSTOM_DATA)!!.get().copyTag().getCompound("storedData").getString("mode") == "retained")
        check(entity.access.fuelLevel == 1234 && entity.getItem(0).count == 11)
    }

    private fun state(owner: String) = DataComponentPatch.builder().set(
        DataComponents.CUSTOM_DATA,
        CustomData.of(
            CompoundTag().apply {
                putInt("fuel", 42)
                putString("owner", owner)
            },
        ),
    ).build()

    private fun placeTurtle(helper: GameTestHelper): TurtleBlockEntity {
        val pos = BlockPos(1, 1, 1)
        helper.setBlock(pos, ModRegistry.Blocks.TURTLE_NORMAL.get())
        return helper.level.getBlockEntity(helper.absolutePos(pos)) as TurtleBlockEntity
    }

    // Upgrades normally come from a datapack registry; a private registry gives the bound holder CC requires.
    private fun <T : UpgradeBase> holder(registry: ResourceKey<Registry<T>>, upgrade: T): Holder.Reference<T> = MappedRegistry(registry, Lifecycle.stable())
        .register(ResourceKey.create(registry, ResourceLocation.fromNamespaceAndPath("tweakium", "test")), upgrade, RegistrationInfo.BUILT_IN)

    private fun statefulTurtleUpgrade() = object : StatefulTurtleUpgrade<IOwnedPeripheral<*>>(ResourceLocation.fromNamespaceAndPath("tweakium", "test_turtle"), TurtleUpgradeType.PERIPHERAL, ItemStack(Items.DIAMOND)) {
        override fun buildPeripheral(turtle: ITurtleAccess, side: TurtleSide): IOwnedPeripheral<*> = error("No peripheral construction needed for item persistence")
        override fun createPeripheral(turtle: ITurtleAccess, side: TurtleSide): IPeripheral? = null
        override fun getType(): UpgradeType<out ITurtleUpgrade> = error("No registration needed for item persistence")
    }

    private fun statefulPocketUpgrade() = object : StatefulPocketUpgrade<IOwnedPeripheral<*>>(ResourceLocation.fromNamespaceAndPath("tweakium", "test_pocket"), ItemStack(Items.DIAMOND)) {
        override fun getPeripheral(access: IPocketAccess): IOwnedPeripheral<*> = error("No peripheral construction needed for item persistence")
        override fun getType(): UpgradeType<out IPocketUpgrade> = error("No registration needed for item persistence")
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
