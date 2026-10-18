package site.siredvin.tweakium.clienttest;

@net.neoforged.fml.common.Mod("tweakium_client_testmod")
public final class ForgeClientMod {
    public ForgeClientMod() {
        site.siredvin.tweakium.ForgeTweakium.INSTANCE.sayHi();
        ClientFixture.register(site.siredvin.tweakium.modules.platform.ForgeTweakiumPlatform.INSTANCE);
    }
}
