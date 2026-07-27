package site.siredvin.broccolium.testmod;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraftforge.fml.common.Mod;
import site.siredvin.testiarium.ForgeTestiarium;
import site.siredvin.testiarium.Testiarium;

@Mod("broccolium_testmod")
public final class BroccoliumTestiariumForgeTestMod {
    public BroccoliumTestiariumForgeTestMod() {
        Testiarium.register(GameTests.class);
        ForgeTestiarium.registerTests();
    }

    public static final class GameTests {
        @GameTest(template = "empty")
        public void loads(GameTestHelper helper) {
            helper.succeed();
        }
    }
}
