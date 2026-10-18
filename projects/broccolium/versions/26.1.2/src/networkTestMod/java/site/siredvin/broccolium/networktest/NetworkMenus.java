package site.siredvin.broccolium.networktest;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import java.util.function.Supplier;

/** The example consumer owns schema validation, not a new library wire protocol. */
public final class NetworkMenus {
    public static Supplier<MenuType<ChestMenu>> type;
    public static FriendlyByteBuf lastBuffer;
    public static int decoded;

    public static ChestMenu build(int id, net.minecraft.world.entity.player.Inventory inventory, FriendlyByteBuf data) {
        lastBuffer = data;
        // Fixed-size, position + slot-count schema. Validate before allocating a container.
        if (data.readableBytes() != 12) throw new IllegalArgumentException("Expected position and count only");
        var position = data.readBlockPos();
        int slots = data.readInt();
        if (!position.equals(new net.minecraft.core.BlockPos(0, 65, 0)) || slots != 27) throw new IllegalArgumentException("Invalid fixture position/count");
        if (!(data instanceof RegistryFriendlyByteBuf registry) || registry.registryAccess() != inventory.player.registryAccess()) throw new AssertionError("Lost registry context");
        decoded++;
        if (NetworkFixture.enabled()) NetworkFixture.append(inventory.player.getGameProfile().name() + "-decode.log", "decode thread=" + Thread.currentThread().getName() + " id=" + id + " slots=" + slots + " pos=" + position);
        return new ChestMenu(type.get(), id, inventory, new SimpleContainer(slots), 3);
    }

    public static void checks(ServerPlayer player) {
        var encoded = new FriendlyByteBuf(io.netty.buffer.Unpooled.buffer());
        byte[] valid;
        try {
            encoded.writeBlockPos(new net.minecraft.core.BlockPos(0, 65, 0));
            encoded.writeInt(27);
            valid = new byte[encoded.readableBytes()];
            encoded.getBytes(0, valid);
        } finally { encoded.release(); }
        var cases = new java.util.ArrayList<byte[]>();
        for (int size = 0; size < valid.length; size++) cases.add(java.util.Arrays.copyOf(valid, size));
        byte[] negative = valid.clone();
        java.util.Arrays.fill(negative, 8, 12, (byte) 0xff);
        cases.add(negative);
        byte[] excessive = valid.clone();
        java.util.Arrays.fill(excessive, 8, 12, (byte) 0x7f);
        cases.add(excessive);
        byte[] position = valid.clone(); position[0] = 1; cases.add(position);
        cases.add(new byte[32601]);
        NativeMenuChecks.create(player, valid);
        if (lastBuffer.refCnt() != 0) throw new AssertionError("Successful decode leaked borrowed buffer");
        int before = decoded;
        for (byte[] bytes : cases) {
            lastBuffer = null;
            byte[] original = bytes.clone();
            boolean rejected = false;
            try { NativeMenuChecks.create(player, bytes); }
            catch (RuntimeException expected) { rejected = true; }
            if (!rejected || decoded != before || !java.util.Arrays.equals(bytes, original)) throw new AssertionError("Malformed decode accepted/mutated data");
            // Netty's zero-capacity EMPTY_BUFFER is an unreleasable singleton, not an allocation leak.
            if (lastBuffer != null && lastBuffer.capacity() != 0 && lastBuffer.refCnt() != 0) throw new AssertionError("Failed decode leaked borrowed buffer");
        }
        NetworkFixture.append("codec-checks.log", "PASS valid=1 rejected=" + cases.size() + " thread=" + Thread.currentThread().getName() + " exactSchema=BlockPos(0,65,0),Int(27) buffersReleased=true noMutationOnRejection=true");
    }
}
