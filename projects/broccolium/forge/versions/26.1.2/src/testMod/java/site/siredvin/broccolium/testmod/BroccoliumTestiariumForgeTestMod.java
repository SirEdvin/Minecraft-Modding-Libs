package site.siredvin.broccolium.testmod;

import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.IEventBus;
import site.siredvin.testiarium.Testiarium;

@Mod("broccolium_testmod")
public final class BroccoliumTestiariumForgeTestMod {
    public BroccoliumTestiariumForgeTestMod(IEventBus bus) {
        ForgeNativeFixtures.register(bus);
        bus.addListener(ForgeProviders::register);
        Testiarium.register(NativeBehaviorGameTests.class, ForgeProviderGameTests.class);
    }
}
