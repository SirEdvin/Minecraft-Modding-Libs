package site.siredvin.testiarium.fixture;

import net.minecraftforge.fml.common.Mod;
import site.siredvin.testiarium.ForgeTestiarium;
import site.siredvin.testiarium.Testiarium;

@Mod("testiarium_testmod")
public final class ForgeTestiariumTestMod {
    public ForgeTestiariumTestMod() {
        Testiarium.register(StandaloneGameTests.class);
        ForgeTestiarium.registerTests();
    }
}
