package site.siredvin.broccolium.testmod

import com.google.gson.JsonPrimitive
import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps
import net.minecraft.core.BlockPos
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.gametest.framework.GameTestHelper
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.level.material.Fluids
import site.siredvin.broccolium.modules.base.block.GenericBlockEntityBlock
import site.siredvin.broccolium.modules.base.codec.GenericCodec
import site.siredvin.broccolium.modules.storage.fluid.AgnosticFluidStack
import site.siredvin.testiarium.api.GameTest
import site.siredvin.testiarium.api.TestGroup
import site.siredvin.testiarium.api.immediate

@TestGroup("broccolium_native")
class RegistryCodecGameTests {
    @GameTest(template = "empty")
    fun absentTooltipTranslationsDoNotLeakRawKeysForItemsOrBlocks(helper: GameTestHelper) = helper.immediate {
        val tooltip = mutableListOf<Component>()
        NativeFixtures.untranslatedItem.get().appendHoverText(net.minecraft.world.item.ItemStack(NativeFixtures.untranslatedItem.get()), net.minecraft.world.item.Item.TooltipContext.of(helper.level), net.minecraft.world.item.component.TooltipDisplay.DEFAULT, tooltip::add, net.minecraft.world.item.TooltipFlag.NORMAL)
        NativeFixtures.untranslatedBlockItem.get().appendHoverText(net.minecraft.world.item.ItemStack(NativeFixtures.untranslatedBlockItem.get()), net.minecraft.world.item.Item.TooltipContext.of(helper.level), net.minecraft.world.item.component.TooltipDisplay.DEFAULT, tooltip::add, net.minecraft.world.item.TooltipFlag.NORMAL)
        check(tooltip.isEmpty())
    }

    @GameTest(template = "empty")
    fun registeredBlockTooltipHooksAreLazyOrderedAndServerSafe(helper: GameTestHelper) = helper.immediate {
        val item = NativeFixtures.tooltipBlockItem.get()
        val tooltip = mutableListOf<Component>()
        repeat(2) {
            item.appendHoverText(net.minecraft.world.item.ItemStack(item), net.minecraft.world.item.Item.TooltipContext.of(helper.level), net.minecraft.world.item.component.TooltipDisplay.DEFAULT, tooltip::add, net.minecraft.world.item.TooltipFlag.NORMAL)
        }
        check(tooltip.map { it.string } == listOf("block first", "block second", "block first", "block second"))
        check(NativeFixtures.blockTooltipCalls == 1)
        check(item.isEnabled())
        try {
            NativeFixtures.tooltipEnabled = false
            check(!item.isEnabled())
        } finally {
            NativeFixtures.tooltipEnabled = true
        }
    }

    @GameTest(template = "empty")
    fun registeredTooltipHooksAreLazyAndDoNotLoadClientOnServer(helper: GameTestHelper) = helper.immediate {
        val item = NativeFixtures.tooltipItem.get()
        val tooltip = mutableListOf<net.minecraft.network.chat.Component>()
        repeat(2) {
            item.appendHoverText(net.minecraft.world.item.ItemStack(item), net.minecraft.world.item.Item.TooltipContext.of(helper.level), net.minecraft.world.item.component.TooltipDisplay.DEFAULT, tooltip::add, net.minecraft.world.item.TooltipFlag.NORMAL)
        }
        check(tooltip.map { it.string } == listOf("first", "second", "first", "second"))
        check(NativeFixtures.tooltipCalls == 1)
        check(item.isEnabled())
        try {
            NativeFixtures.tooltipEnabled = false
            check(!item.isEnabled())
        } finally {
            NativeFixtures.tooltipEnabled = true
        }
    }

    @GameTest(template = "empty")
    fun registryEntryCodecResolvesRealSupplierAndRejectsUnknown(helper: GameTestHelper) = helper.immediate {
        val codec = GenericCodec.codec(BuiltInRegistries.BLOCK_ENTITY_TYPE)
        val json = codec.encodeStart(JsonOps.INSTANCE, NativeFixtures.type).getOrThrow()
        check(json.asString == "broccolium_testmod:observer")
        val entry = codec.parse(JsonOps.INSTANCE, json).getOrThrow()
        check(entry.get() === NativeFixtures.type.get())
        check(codec.parse(JsonOps.INSTANCE, JsonPrimitive("broccolium_testmod:missing")).error().isPresent)
        val block = NativeFixtures.block.get()
        val before = NativeFixtures.typeResolutions
        val entity = block.newBlockEntity(helper.absolutePos(BlockPos(1, 1, 1)), NativeFixtures.block.get().defaultBlockState())
        check(entity.type === NativeFixtures.type.get() && NativeFixtures.typeResolutions == before + 1)
        val blockCodec = site.siredvin.broccolium.modules.base.codec.BlockCodec.buildCodec<GenericBlockEntityBlock<ObservedEntity>, net.minecraft.world.level.block.entity.BlockEntityType<ObservedEntity>, ObservedEntity> { entry, rotatable, ticking, properties ->
            GenericBlockEntityBlock({ entry }, rotatable, ticking, properties)
        }.codec()
        val encoded = blockCodec.encodeStart(JsonOps.INSTANCE, block).getOrThrow()
        check(encoded.asJsonObject.get("block_entity").asString == "broccolium_testmod:observer")
        check(encoded.asJsonObject.get("isRotatable").asBoolean)
        check(encoded.asJsonObject.get("belongToTickingEntity").asBoolean)
        check(NativeFixtures.typeResolutions == before + 2)
        val patch = DataComponentPatch.builder()
            .set(DataComponents.CUSTOM_NAME, Component.literal("saved fluid"))
            .remove(DataComponents.ENCHANTMENTS)
            .build()
        val fluid = AgnosticFluidStack(Fluids.WATER, 731.0, patch)
        val saved = fluid.save(CompoundTag())
        check(AgnosticFluidStack.of(saved) == fluid) { "Fluid component values/removals did not roundtrip" }
        // An unregistered persistent type is rejected by the real patch encoder.
        val unregistered = DataComponentType.builder<String>().persistent(Codec.STRING).build()
        val invalid = AgnosticFluidStack(Fluids.LAVA, 19.0, DataComponentPatch.builder().set(unregistered, "invalid").build())
        val beforeFailure = saved.copy()
        var rejected = false
        try {
            invalid.save(saved)
        } catch (_: RuntimeException) {
            rejected = true
        }
        check(rejected) { "Unregistered persistent component unexpectedly encoded" }
        check(saved == beforeFailure) { "Encode failure partially mutated caller NBT" }
    }
}
