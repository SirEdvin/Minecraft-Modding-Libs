package site.siredvin.broccolium.testmod

import net.minecraft.core.registries.BuiltInRegistries
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister
import site.siredvin.broccolium.modules.platform.BasePlatform
import site.siredvin.broccolium.modules.platform.ForgeInnerBasePlatform
import site.siredvin.broccolium.modules.platform.ModInformationTracker

object ForgeNativeFixtures {
    @JvmStatic
    fun register(bus: IEventBus) {
        val inner = object : ForgeInnerBasePlatform() {
            override val modID = NativeFixtures.MOD_ID
            override val blocksRegistry = DeferredRegister.create(BuiltInRegistries.BLOCK, modID)
            override val itemsRegistry = DeferredRegister.create(BuiltInRegistries.ITEM, modID)
            override val blockEntityTypesRegistry = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, modID)
        }
        NativeFixtures.register(object : BasePlatform() {
            override val baseInnerPlatform = inner
            override val modInformationTracker = ModInformationTracker()
        })
        inner.blocksRegistry.register(bus)
        inner.itemsRegistry.register(bus)
        inner.blockEntityTypesRegistry.register(bus)
    }
}
