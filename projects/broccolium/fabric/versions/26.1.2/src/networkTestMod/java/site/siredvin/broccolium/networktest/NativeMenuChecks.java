package site.siredvin.broccolium.networktest;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuType;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import site.siredvin.broccolium.modules.platform.FabricInnerBasePlatform;

public final class NativeMenuChecks implements ModInitializer {
    @Override
    public void onInitialize() {
        var platform = new FabricInnerBasePlatform() {
            @Override public String getModID() { return "broccolium_network_testmod"; }
        };
        NetworkMenus.type = platform.registerMenu(Identifier.fromNamespaceAndPath("broccolium_network_testmod", "menu"), NetworkMenus::build);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void create(ServerPlayer player, byte[] bytes) {
        ((ExtendedMenuType) NetworkMenus.type.get()).create(100, player.getInventory(), bytes);
    }
}
