package site.siredvin.broccolium.testmod

import net.fabricmc.api.ModInitializer
import site.siredvin.broccolium.modules.platform.BasePlatform
import site.siredvin.broccolium.modules.platform.FabricInnerBasePlatform
import site.siredvin.broccolium.modules.platform.ModInformationTracker
import site.siredvin.testiarium.FabricTestiarium
import site.siredvin.testiarium.Testiarium

object BroccoliumTestiariumFabricTestMod : ModInitializer {
    override fun onInitialize() {
        site.siredvin.broccolium.FabricBroccolium.sayHi()
        NativeFixtures.register(object : BasePlatform() {
            override val baseInnerPlatform = object : FabricInnerBasePlatform() {
                override val modID = NativeFixtures.MOD_ID
            }
            override val modInformationTracker = ModInformationTracker()
        })
        FabricProviders.register()
        Testiarium.register(
            ReverseItemBridgeGameTests::class.java,
            ItemBridgeSlotGameTests::class.java,
            ItemBridgeContractGameTests::class.java,
            EnergyIntegrationGameTests::class.java,
            NativeBehaviorGameTests::class.java,
            RegistryCodecGameTests::class.java,
            FabricProviderGameTests::class.java,
        )
        FabricTestiarium.registerTests()
    }
}
