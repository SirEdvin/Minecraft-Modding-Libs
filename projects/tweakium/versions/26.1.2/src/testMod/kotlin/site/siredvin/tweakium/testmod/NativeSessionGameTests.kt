package site.siredvin.tweakium.testmod

import com.mojang.authlib.GameProfile
import dan200.computercraft.shared.ModRegistry
import dan200.computercraft.shared.turtle.blocks.TurtleBlockEntity
import dan200.computercraft.shared.turtle.core.TurtleBrain
import net.minecraft.core.BlockPos
import net.minecraft.core.component.DataComponents
import net.minecraft.gametest.framework.GameTestHelper
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.EquipmentSlotGroup
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.ItemAttributeModifiers
import site.siredvin.testiarium.api.GameTest
import site.siredvin.testiarium.api.TestGroup
import site.siredvin.testiarium.api.immediate
import site.siredvin.tweakium.modules.player.FakePlayerProviderTurtle
import java.util.UUID

@TestGroup("tweakium")
class NativeSessionGameTests {
    @GameTest(template = "modulebehavior.empty")
    fun nativeEntityStorageLoansReturnToolsAndDropOnlyExactOverflowOnAllExits(helper: GameTestHelper) = helper.immediate {
        @Suppress("DEPRECATION")
        val realPlayer = helper.makeMockServerPlayerInLevel()
        NativeUpgradeFixtures.configurePlayer.accept(realPlayer)
        val pos = helper.absolutePos(BlockPos(1, 1, 1))
        realPlayer.setPos(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble())
        val cart = checkNotNull(net.minecraft.world.entity.EntityType.CHEST_MINECART.create(helper.level, net.minecraft.world.entity.EntitySpawnReason.COMMAND))
        cart.setPos(pos.center)
        helper.level.addFreshEntity(cart)
        try {
            for (failure in listOf(false, true)) {
                for (slot in 0 until cart.containerSize) cart.setItem(slot, ItemStack(Items.DIAMOND, 64))
                var actor: net.minecraft.server.level.ServerPlayer? = null
                val sentinel = IllegalStateException("native storage loan sentinel")
                try {
                    site.siredvin.tweakium.modules.player.FakePlayerProviderEntity.withPlayer(cart, realPlayer, { proxy ->
                        actor = proxy.fakePlayer
                        check(cart.isEmpty) { "Native source was copied rather than loaned" }
                        proxy.fakePlayer.inventory.setItem(0, ItemStack(Items.EMERALD, 2))
                        proxy.fakePlayer.inventory.setItem(40, ItemStack(Items.GOLD_INGOT, 5))
                        if (failure) throw sentinel
                    })
                    check(!failure)
                } catch (caught: IllegalStateException) { check(failure && caught === sentinel) }
                check(cart.getItem(0).`is`(Items.EMERALD) && cart.getItem(0).count == 2)
                check((1 until cart.containerSize).sumOf { cart.getItem(it).count } == (cart.containerSize - 1) * 64)
                check(actor!!.inventory.isEmpty)
                val drops = helper.level.getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity::class.java, net.minecraft.world.phys.AABB(pos).inflate(3.0))
                    .filter { it.item.`is`(Items.GOLD_INGOT) }
                check(drops.sumOf { it.item.count } == 5) { "Native overflow was duplicated or discarded" }
                drops.forEach { it.discard() }
            }
        } finally {
            cart.clearContent()
            cart.discard()
            realPlayer.inventory.clearContent()
            helper.level.server.playerList.remove(realPlayer)
            realPlayer.connection.disconnect(net.minecraft.network.chat.Component.literal("R3 storage fixture complete"))
            realPlayer.discard()
        }
    }

    private fun turtle(helper: GameTestHelper): TurtleBrain {
        val pos = helper.absolutePos(BlockPos(1, 1, 1))
        helper.level.setBlockAndUpdate(pos, ModRegistry.Blocks.TURTLE_NORMAL.get().defaultBlockState())
        return (helper.level.getBlockEntity(pos) as TurtleBlockEntity).access as TurtleBrain
    }

    @GameTest(template = "modulebehavior.empty")
    fun nativeSelectedSlotsRestoreReplacedToolsAndPermanentAttributesOnAllExits(helper: GameTestHelper) = helper.immediate {
        val turtle = turtle(helper)
        val modifierId = Identifier.fromNamespaceAndPath("tweakium", "native_tool")
        val previous = AttributeModifier(modifierId, 2.0, AttributeModifier.Operation.ADD_VALUE)
        FakePlayerProviderTurtle.withPlayer(turtle, { proxy ->
            proxy.fakePlayer.attributes.getInstance(Attributes.ATTACK_DAMAGE)!!.addPermanentModifier(previous)
        }, skipInventory = true)
        for (slot in listOf(0, 8, 9, 15)) for (failure in listOf(false, true)) {
            turtle.inventory.clearContent()
            turtle.inventory.setItem(0, ItemStack(Items.STICK, 3))
            turtle.inventory.setItem(slot, ItemStack(Items.DIAMOND, 4).apply {
                set(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.builder().add(Attributes.ATTACK_DAMAGE,
                    AttributeModifier(modifierId, 5.0, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND).build())
            })
            turtle.selectedSlot = slot
            var actor: net.minecraft.server.level.ServerPlayer? = null
            val sentinel = IllegalStateException("native session sentinel")
            try {
                FakePlayerProviderTurtle.withPlayer(turtle, { proxy ->
                    val player = proxy.fakePlayer
                    actor = player
                    check(player.inventory.selectedSlot == 0 && player.mainHandItem.`is`(Items.DIAMOND)) { "Native selected slot $slot was clamped instead of remapped" }
                    check(player.attributes.getInstance(Attributes.ATTACK_DAMAGE)!!.getModifier(modifierId)!!.amount() == 5.0)
                    player.inventory.setItem(0, ItemStack(Items.EMERALD, 2))
                    player.inventory.setItem(20, ItemStack(Items.GOLD_INGOT, 5))
                    if (failure) throw sentinel
                })
                check(!failure)
            } catch (caught: IllegalStateException) {
                check(failure && caught === sentinel)
            }
            check(turtle.inventory.getItem(slot).`is`(Items.EMERALD) && turtle.inventory.getItem(slot).count == 2)
            if (slot != 0) check(turtle.inventory.getItem(0).`is`(Items.STICK) && turtle.inventory.getItem(0).count == 3)
            check((0 until turtle.inventory.containerSize).sumOf { if (turtle.inventory.getItem(it).`is`(Items.GOLD_INGOT)) turtle.inventory.getItem(it).count else 0 } == 5)
            check(actor!!.inventory.isEmpty) { "Session left duplicate inventory on cached native player" }
            val instance = actor.attributes.getInstance(Attributes.ATTACK_DAMAGE)!!
            check(instance.getModifier(modifierId) == previous && previous in instance.permanentModifiers)
        }
        turtle.inventory.clearContent()
    }

    @GameTest(template = "modulebehavior.empty")
    fun changedSelectionDuringNativeActionDoesNotRemapToAnotherSourceSlot(helper: GameTestHelper) = helper.immediate {
        val turtle = turtle(helper)
        turtle.inventory.setItem(0, ItemStack(Items.STICK, 3))
        turtle.inventory.setItem(15, ItemStack(Items.DIAMOND, 4))
        turtle.selectedSlot = 15
        FakePlayerProviderTurtle.withPlayer(turtle, { proxy ->
            proxy.fakePlayer.mainHandItem.shrink(1)
            turtle.selectedSlot = 8
        })
        check(turtle.inventory.getItem(15).`is`(Items.DIAMOND) && turtle.inventory.getItem(15).count == 3) { "Changed native selection moved the borrowed tool to another source slot" }
        check(turtle.inventory.getItem(0).`is`(Items.STICK) && turtle.inventory.getItem(8).isEmpty)
        turtle.inventory.clearContent()
    }

    @GameTest(template = "modulebehavior.empty")
    fun changedNativeOwnerCannotReusePreviousPlayersIdentity(helper: GameTestHelper) = helper.immediate {
        val turtle = turtle(helper)
        for (name in listOf("FirstOwner", "NextOwner")) {
            val profile = GameProfile(UUID.randomUUID(), name)
            turtle.setOwningPlayer(profile)
            FakePlayerProviderTurtle.withPlayer(turtle, { proxy ->
                check(proxy.fakePlayer.gameProfile == profile) { "Cached fake player retained previous turtle owner identity" }
            })
        }
    }
}
