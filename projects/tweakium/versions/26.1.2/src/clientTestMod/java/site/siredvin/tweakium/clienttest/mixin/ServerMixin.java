package site.siredvin.tweakium.clienttest.mixin;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class ServerMixin {
    @Inject(method = "tickServer", at = @At("RETURN"))
    private void r7Tick(java.util.function.BooleanSupplier keepTicking, CallbackInfo callback) { site.siredvin.tweakium.clienttest.ClientFixture.tick((MinecraftServer) (Object) this); }
}
