package site.siredvin.tweakium.modules.platform

import net.minecraft.advancements.CriterionTrigger
import net.minecraft.core.component.DataComponentType
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType
import net.neoforged.neoforge.registries.DeferredRegister
import site.siredvin.tweakium.ForgeTweakium
import site.siredvin.tweakium.TweakiumCore

object ForgeTweakiumPlatform : ForgeInnerComputerBasePlatform() {
    override val modID: String
        get() = TweakiumCore.MOD_ID
    override val blocksRegistry: DeferredRegister<Block>
        get() = ForgeTweakium.blocksRegistry
    override val blockEntityTypesRegistry: DeferredRegister<BlockEntityType<*>>
        get() = ForgeTweakium.blockEntityTypesRegistry
    override val itemsRegistry: DeferredRegister<Item>
        get() = ForgeTweakium.itemsRegistry
    override val dataComponentTypesRegistry: DeferredRegister<DataComponentType<*>>
        get() = ForgeTweakium.dataComponentTypesRegistry
    override val criterionTriggers: DeferredRegister<CriterionTrigger<*>>
        get() = ForgeTweakium.criterionTriggers
}
