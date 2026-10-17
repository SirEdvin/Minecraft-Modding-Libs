package site.siredvin.tweakium

import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.ItemStackTemplate
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.CustomData
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import site.siredvin.broccolium.test.WithMinecraft
import site.siredvin.tweakium.modules.peripheral.util.UpgradeTemplateUtil
import java.util.Optional

@WithMinecraft
class UpgradeTemplateTests {
    private val important = listOf(DataComponents.CUSTOM_DATA)

    @Test
    fun emptyUpgradeDataKeepsImmutableCraftingTemplate() {
        val crafting = ItemStackTemplate(Items.DIAMOND)
        assertSame(crafting, UpgradeTemplateUtil.write(crafting, DataComponentPatch.EMPTY, important))
        assertTrue(UpgradeTemplateUtil.read(crafting, crafting, important).isEmpty)
    }

    @Test
    fun customDataSurvivesRealTemplateCodecRoundTrip() {
        val crafting = ItemStackTemplate(Items.DIAMOND)
        val data = DataComponentPatch.builder().set(DataComponents.CUSTOM_DATA, customData("oak")).build()
        val result = UpgradeTemplateUtil.write(crafting, data, important)
        val restored = roundTrip(result)
        assertEquals(data, UpgradeTemplateUtil.read(restored, crafting, important))
        assertNull(crafting.get(DataComponents.CUSTOM_DATA))
        assertEquals("oak", restored.create().get(DataComponents.CUSTOM_DATA)!!.copyTag().getStringOr("owner", ""))
    }

    @Test
    fun explicitRemovalSurvivesTemplateAndMutableStackSerialization() {
        val defaults = DataComponentPatch.builder().set(DataComponents.CUSTOM_DATA, customData("default")).build()
        val crafting = ItemStackTemplate(Items.DIAMOND, defaults)
        val removal = DataComponentPatch.builder().remove(DataComponents.CUSTOM_DATA).build()
        val withoutData = roundTrip(UpgradeTemplateUtil.write(crafting, removal, important))
        val mutable = withoutData.create()
        assertFalse(mutable.has(DataComponents.CUSTOM_DATA))
        val ops = WithMinecraft.Setup.registries().createSerializationContext(NbtOps.INSTANCE)
        val encoded = ItemStack.CODEC.encodeStart(ops, mutable).getOrThrow()
        val restored = ItemStackTemplate.fromNonEmptyStack(ItemStack.CODEC.parse(ops, encoded).getOrThrow())
        val data = UpgradeTemplateUtil.read(restored, crafting, important)
        assertEquals(Optional.empty<Any>(), data.entrySet().single { it.key == DataComponents.CUSTOM_DATA }.value)
        assertNull(UpgradeTemplateUtil.write(crafting, data, important).get(DataComponents.CUSTOM_DATA))
        assertEquals("default", crafting.get(DataComponents.CUSTOM_DATA)!!.copyTag().getStringOr("owner", ""))
    }

    @Test
    fun absentComponentRelativeToCraftingDefaultBecomesRemoval() {
        val crafting = ItemStackTemplate(Items.DIAMOND, DataComponentPatch.builder().set(DataComponents.CUSTOM_DATA, customData("default")).build())
        val data = UpgradeTemplateUtil.read(ItemStackTemplate(Items.DIAMOND), crafting, important)
        assertEquals(Optional.empty<Any>(), data.entrySet().single { it.key == DataComponents.CUSTOM_DATA }.value)
        assertNull(UpgradeTemplateUtil.write(crafting, data, important).get(DataComponents.CUSTOM_DATA))
    }

    @Test
    fun unapprovedDataCannotReplaceUnrelatedCraftingComponents() {
        val name = Component.literal("crafting name")
        val defaults = DataComponentPatch.builder()
            .set(DataComponents.CUSTOM_NAME, name)
            .remove(DataComponents.RARITY)
            .build()
        val crafting = ItemStackTemplate(Items.DIAMOND, defaults).withCount(3)
        val data = DataComponentPatch.builder()
            .set(DataComponents.CUSTOM_DATA, customData("new"))
            .set(DataComponents.CUSTOM_NAME, Component.literal("must be ignored"))
            .set(DataComponents.DAMAGE, 8)
            .build()
        val result = UpgradeTemplateUtil.write(crafting, data, important)
        assertEquals(3, result.count())
        assertEquals(name, result.get(DataComponents.CUSTOM_NAME))
        assertEquals(Optional.empty<Any>(), result.components().entrySet().single { it.key == DataComponents.RARITY }.value)
        assertFalse(result.components().entrySet().any { it.key == DataComponents.DAMAGE })
        assertEquals(defaults, crafting.components())
    }

    @Test
    fun explicitRemovalWithoutCraftingDefaultRemainsExplicit() {
        val crafting = ItemStackTemplate(Items.DIAMOND)
        val removal = DataComponentPatch.builder().remove(DataComponents.CUSTOM_DATA).build()
        val restored = roundTrip(UpgradeTemplateUtil.write(crafting, removal, important))
        assertEquals(Optional.empty<Any>(), UpgradeTemplateUtil.read(restored, crafting, important).entrySet().single { it.key == DataComponents.CUSTOM_DATA }.value)
    }

    private fun roundTrip(template: ItemStackTemplate): ItemStackTemplate {
        val ops = WithMinecraft.Setup.registries().createSerializationContext(NbtOps.INSTANCE)
        val encoded = ItemStackTemplate.CODEC.encodeStart(ops, template).getOrThrow()
        return ItemStackTemplate.CODEC.parse(ops, encoded).getOrThrow()
    }

    private fun customData(owner: String): CustomData = CustomData.of(CompoundTag().apply { putString("owner", owner) })
}
