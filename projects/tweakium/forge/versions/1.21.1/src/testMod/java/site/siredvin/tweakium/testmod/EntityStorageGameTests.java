package site.siredvin.tweakium.testmod;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.vehicle.MinecartChest;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import site.siredvin.broccolium.modules.storage.ForgeStorageUtils;
import site.siredvin.testiarium.api.TestGroup;

@TestGroup("tweakium")
public final class EntityStorageGameTests {
    @GameTest(template = "empty")
    public void readsChestMinecartInventory(GameTestHelper helper) {
        var cart = new MinecartChest(helper.getLevel(), 0, 0, 0);
        cart.setItem(0, new ItemStack(Items.DIAMOND, 3));
        var storage = ForgeStorageUtils.INSTANCE.extractStorageFromEntity(helper.getLevel(), cart, null);
        helper.assertTrue(storage != null, "Chest minecart item capability must be available");
        var item = storage.getContent().next();
        helper.assertTrue(item.is(Items.DIAMOND) && item.getCount() == 3, "Entity storage must expose the minecart contents");
        helper.succeed();
    }
}
