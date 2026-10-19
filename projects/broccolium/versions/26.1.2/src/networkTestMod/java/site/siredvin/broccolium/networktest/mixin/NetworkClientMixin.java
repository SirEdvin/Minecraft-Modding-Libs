package site.siredvin.broccolium.networktest.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import site.siredvin.broccolium.networktest.NetworkClient;

@Mixin(Minecraft.class)
public abstract class NetworkClientMixin {
    @Inject(method = "tick", at = @At("RETURN"))
    private void networkTick(CallbackInfo callback) { NetworkClient.tick(); }
}
