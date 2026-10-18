package site.siredvin.tweakium.clienttest;

public final class FabricClientMod implements net.fabricmc.api.ModInitializer {
    @Override public void onInitialize() {
        site.siredvin.tweakium.modules.FabricTweakium.INSTANCE.sayHi();
        ClientFixture.register(site.siredvin.tweakium.modules.platform.FabricTweakiumPlatform.INSTANCE);
    }
}
