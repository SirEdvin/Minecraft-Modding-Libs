package site.siredvin.tweakium.clienttest;

@net.neoforged.fml.common.Mod("tweakium_client_testmod")
public final class ForgeClientMod {
    public ForgeClientMod(net.neoforged.bus.api.IEventBus bus) {
        site.siredvin.tweakium.ForgeTweakium.INSTANCE.sayHi();
        // Consumers opt into only the registries they use; the production Tweakium platform has no menus.
        var menus = net.neoforged.neoforge.registries.DeferredRegister.create(net.minecraft.core.registries.BuiltInRegistries.MENU, "tweakium");
        ClientFixture.register(new site.siredvin.tweakium.modules.platform.ForgeInnerComputerBasePlatform() {
            @Override public String getModID() { return "tweakium"; }
            @Override public net.neoforged.neoforge.registries.DeferredRegister<net.minecraft.world.inventory.MenuType<?>> getMenuTypes() { return menus; }
            @Override public net.neoforged.neoforge.registries.DeferredRegister<net.minecraft.world.item.Item> getItemsRegistry() { return site.siredvin.tweakium.ForgeTweakium.INSTANCE.getItemsRegistry(); }
        });
        menus.register(bus);
    }
}
