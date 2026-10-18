package site.siredvin.broccolium.networktest.mixin;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import site.siredvin.broccolium.networktest.NetworkFixture;
import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public abstract class NetworkServerMixin {
    @Inject(method = "tickServer", at = @At("RETURN"))
    private void networkTick(BooleanSupplier keepTicking, CallbackInfo callback) {
        NetworkFixture.tick((MinecraftServer) (Object) this);
    }
}
