package site.siredvin.testiarium.cct;

import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import site.siredvin.testiarium.ForgeTestiarium;
import site.siredvin.testiarium.Testiarium;

@Mod("testiarium_cct_testmod")
public final class ForgeCctTestMod {
    public ForgeCctTestMod() {
        if (!ModList.get().isLoaded("computercraft")) return;
        Testiarium.register(PeripheralGameTests.class);
        ForgeTestiarium.registerTests();
    }

    public static final class PeripheralGameTests {
        @GameTest(template = "empty")
        public void publicPeripheralApi(GameTestHelper helper) {
            if (!IPeripheral.class.isInterface()) throw new AssertionError("IPeripheral must be an interface");
            helper.succeed();
        }
    }
}
