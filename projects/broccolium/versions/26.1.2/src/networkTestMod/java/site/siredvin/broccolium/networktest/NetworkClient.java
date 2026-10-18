package site.siredvin.broccolium.networktest;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.HashedPatchMap;
import net.minecraft.network.HashedStack;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import java.nio.file.Files;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/** Sends actual native packets from an actual Minecraft client's main thread. */
public final class NetworkClient {
    private static int sent = -1;
    private static int observed = -1;
    private static int wait;
    private static boolean screenRegistered;

    public static void tick() {
        if (!NetworkFixture.enabled()) return;
        Minecraft minecraft = Minecraft.getInstance();
        registerScreen();
        if (Files.exists(NetworkFixture.directory().resolve("done"))) { minecraft.stop(); return; }
        if (minecraft.player == null || minecraft.getConnection() == null) return;
        try {
            if (!minecraft.isSameThread()) throw new AssertionError("Not on client thread");
            var path = NetworkFixture.directory().resolve("phase.properties");
            if (!Files.exists(path)) return;
            Properties control = new Properties();
            try (var reader = Files.newBufferedReader(path)) { control.load(reader); }
            int phase = Integer.parseInt(control.getProperty("phase"));
            if (sent == phase) return;
            if (observed != phase) { observed = phase; wait = 0; }
            if (++wait < 30) return;
            String name = minecraft.player.getGameProfile().name();
            if (wait == 30) Files.writeString(NetworkFixture.directory().resolve(name + ".ready"), Integer.toString(phase));
            for (String peer : new String[]{"R6Alice", "R6Bob"}) {
                var ready = NetworkFixture.directory().resolve(peer + ".ready");
                if (!Files.exists(ready) || !Files.readString(ready).equals(Integer.toString(phase))) return;
            }
            var barrier = NetworkFixture.directory().resolve("send-time-" + phase);
            try {
                Files.writeString(barrier, Long.toString(System.currentTimeMillis() + 1000), java.nio.file.StandardOpenOption.CREATE_NEW);
            } catch (java.nio.file.FileAlreadyExistsException expected) { }
            String timestamp = Files.readString(barrier);
            if (timestamp.length() != 13) return; // The other process may still be publishing its single write.
            long scheduled = Long.parseLong(timestamp);
            long actual = System.currentTimeMillis();
            if (actual < scheduled) return;
            if (actual - scheduled > 250) throw new AssertionError("Client missed simultaneous-send barrier");
            NetworkFixture.append(name + "-barrier.log", "phase=" + phase + " scheduled=" + scheduled + " actual=" + actual);
            int id = Integer.parseInt(control.getProperty("id." + name));
            String action = control.getProperty("action");
            int state = minecraft.player.containerMenu.getStateId();
            if (action.equals("position")) {
                var packet = new ServerboundUseItemOnPacket(InteractionHand.MAIN_HAND, new BlockHitResult(new Vec3(0.5, 65.5, 0.5), Direction.UP, new BlockPos(0, 65, 0), false), phase + 1);
                minecraft.getConnection().send(packet);
                NetworkFixture.append(name + "-events.log", "send phase=" + phase + " " + packet + " thread=" + Thread.currentThread().getName());
            } else {
                short slot = (short) (action.equals("slot_high") ? 32767 : action.equals("slot_negative") ? -2 : 0);
                byte button = (byte) (action.equals("button") ? 127 : 0);
                var changed = new Int2ObjectOpenHashMap<HashedStack>();
                HashedStack carried = HashedStack.EMPTY;
                if (action.equals("forged_count")) {
                    carried = new HashedStack.ActualItem(Items.DIAMOND.builtInRegistryHolder(), Integer.MAX_VALUE, new HashedPatchMap(Map.of(), Set.of()));
                    changed.put(0, carried);
                }
                var packet = new ServerboundContainerClickPacket(id, state, slot, button, action.equals("button") || action.equals("forged_count") ? ContainerInput.PICKUP : ContainerInput.QUICK_MOVE, changed, carried);
                minecraft.getConnection().send(packet);
                NetworkFixture.append(name + "-events.log", "send phase=" + phase + " " + packet + " uuid=" + minecraft.player.getUUID() + " thread=" + Thread.currentThread().getName());
            }
            sent = phase;
            Files.writeString(NetworkFixture.directory().resolve(name + ".sent"), Integer.toString(phase));
        } catch (Exception failure) { throw new AssertionError("Actual client fixture failed", failure); }
    }

    // Test-only access to vanilla's private registration method, isolated to the physical client.
    private static void registerScreen() {
        if (screenRegistered) return;
        try {
            var constructor = Class.forName("net.minecraft.client.gui.screens.MenuScreens$ScreenConstructor");
            var factory = java.lang.reflect.Proxy.newProxyInstance(constructor.getClassLoader(), new Class<?>[]{constructor}, (proxy, method, arguments) -> {
                if (method.getName().equals("create")) return new net.minecraft.client.gui.screens.inventory.ContainerScreen((net.minecraft.world.inventory.ChestMenu) arguments[0], (net.minecraft.world.entity.player.Inventory) arguments[1], (net.minecraft.network.chat.Component) arguments[2]);
                throw new UnsupportedOperationException(method.toString());
            });
            var register = net.minecraft.client.gui.screens.MenuScreens.class.getDeclaredMethod("register", net.minecraft.world.inventory.MenuType.class, constructor);
            register.setAccessible(true);
            register.invoke(null, NetworkMenus.type.get(), factory);
            screenRegistered = true;
        } catch (ReflectiveOperationException exception) { throw new AssertionError(exception); }
    }
}
