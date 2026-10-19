package site.siredvin.broccolium.networktest;

import io.netty.buffer.Unpooled;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;

public final class NativeMenuChecks {
    public static void create(ServerPlayer player, byte[] bytes) {
        var buffer = new RegistryFriendlyByteBuf(Unpooled.wrappedBuffer(bytes), player.registryAccess());
        try {
            ((IMenuTypeExtension<?>) NetworkMenus.type.get()).create(100, player.getInventory(), buffer);
            if (buffer.refCnt() != 1) throw new AssertionError("Menu builder released caller-owned buffer");
        } finally { buffer.release(); }
    }
}
