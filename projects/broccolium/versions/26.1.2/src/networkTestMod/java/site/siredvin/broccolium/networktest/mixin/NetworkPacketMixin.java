package site.siredvin.broccolium.networktest.mixin;

import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import site.siredvin.broccolium.networktest.NetworkFixture;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class NetworkPacketMixin {
    @Shadow public ServerPlayer player;

    @Inject(method = "handleContainerClick", at = @At("RETURN"))
    private void clicked(ServerboundContainerClickPacket packet, CallbackInfo callback) {
        NetworkFixture.received(player, packet.toString());
    }

    @Inject(method = "handleUseItemOn", at = @At("RETURN"))
    private void used(ServerboundUseItemOnPacket packet, CallbackInfo callback) {
        NetworkFixture.received(player, packet.toString());
    }
}
