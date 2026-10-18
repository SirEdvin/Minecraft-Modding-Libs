package site.siredvin.broccolium.networktest;

import net.neoforged.fml.common.Mod;

@Mod("broccolium_network_testmod")
public final class ForgeNetworkMod {
    public ForgeNetworkMod(net.neoforged.bus.api.IEventBus bus) {
        var registry = net.neoforged.neoforge.registries.DeferredRegister.create(net.minecraft.core.registries.BuiltInRegistries.MENU, "broccolium_network_testmod");
        var platform = new site.siredvin.broccolium.modules.platform.ForgeInnerBasePlatform() {
            @Override public String getModID() { return "broccolium_network_testmod"; }
            @Override public net.neoforged.neoforge.registries.DeferredRegister<net.minecraft.world.inventory.MenuType<?>> getMenuTypes() { return registry; }
        };
        NetworkMenus.type = platform.registerMenu(net.minecraft.resources.Identifier.fromNamespaceAndPath("broccolium_network_testmod", "menu"), NetworkMenus::build);
        registry.register(bus);
    }
}
