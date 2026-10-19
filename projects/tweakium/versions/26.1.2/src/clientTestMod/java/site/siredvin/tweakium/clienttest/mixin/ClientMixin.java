package site.siredvin.tweakium.clienttest.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class ClientMixin {
    @Inject(method = "tick", at = @At("RETURN"))
    private void r7Tick(CallbackInfo callback) { site.siredvin.tweakium.clienttest.ClientProof.tick(); }
}
