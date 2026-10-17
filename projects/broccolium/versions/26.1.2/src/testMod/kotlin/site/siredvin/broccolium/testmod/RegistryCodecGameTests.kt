package site.siredvin.broccolium.testmod

import com.google.gson.JsonPrimitive
import com.mojang.serialization.JsonOps
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.gametest.framework.GameTestHelper
import net.minecraft.resources.ResourceKey
import net.minecraft.core.registries.Registries
import net.minecraft.world.level.block.state.BlockBehaviour
import site.siredvin.broccolium.modules.base.block.GenericBlockEntityBlock
import site.siredvin.broccolium.modules.base.codec.GenericCodec
import site.siredvin.testiarium.api.GameTest
import site.siredvin.testiarium.api.TestGroup
import site.siredvin.testiarium.api.immediate

@TestGroup("broccolium_native")
class RegistryCodecGameTests {
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
    }
}
