package site.siredvin.broccolium.networktest;

import com.google.gson.GsonBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/** Task-local real TCP-client fixture. No custom network payload/receiver. */
public final class NetworkFixture {
    private static final String[] PHASES = {"concurrent", "wrong_id", "slot_high", "slot_negative", "button", "forged_count", "stale_menu", "distance", "removed", "spectator", "position", "player_identity"};
    private static final List<Map<String, Object>> results = new ArrayList<>();
    private static final List<ChestBlockEntity> chests = new ArrayList<>();
    private static int phase = -1;
    private static int started;
    private static int received;
    private static List<ItemStack> before;
    private static boolean finished;
    private static final Path directory = System.getProperty("broccolium.network.dir") == null ? null : Path.of(System.getProperty("broccolium.network.dir"));

    public static boolean enabled() { return directory != null; }

    public static void received(ServerPlayer player, String packet) {
        if (!enabled()) return;
        if (!player.level().getServer().isSameThread()) throw new AssertionError("Packet mutation thread is not server thread");
        received++;
        append("server-events.log", "receive tick=" + player.level().getServer().getTickCount() + " phase=" + phase + " uuid=" + player.getUUID() + " thread=" + Thread.currentThread().getName() + " " + packet);
    }

    public static void tick(MinecraftServer server) {
        if (!enabled() || finished) return;
        try {
            if (!server.isSameThread()) throw new AssertionError("Fixture not on server thread");
            List<ServerPlayer> players = server.getPlayerList().getPlayers().stream().filter(p -> p.getGameProfile().name().startsWith("R6")).sorted(java.util.Comparator.comparing(p -> p.getGameProfile().name())).toList();
            if (players.size() != 2) return;
            if (phase < 0) {
                NetworkMenus.checks(players.get(0));
                phase = 0;
                setup(server, players);
                return;
            }
            if (server.getTickCount() - started < 80) return;
            for (ServerPlayer player : players) {
                Path ack = directory.resolve(player.getGameProfile().name() + ".sent");
                if (!Files.exists(ack) || !Files.readString(ack).equals(Integer.toString(phase))) return;
            }
            if (received != 2) throw new AssertionError("Expected exactly two real received packets, got " + received);
            int chestCount = chests.stream().mapToInt(c -> c.countItem(Items.DIAMOND) + c.countItem(Items.EMERALD)).sum();
            int inventoryCount = players.stream().mapToInt(p -> p.getInventory().countItem(Items.DIAMOND) + p.getInventory().countItem(Items.EMERALD)).sum();
            int carriedCount = players.stream().mapToInt(p -> p.containerMenu.getCarried().getCount()).sum();
            int expected = PHASES[phase].equals("player_identity") ? 49 : 32;
            if (chestCount + inventoryCount + carriedCount != expected) throw new AssertionError("Conservation: " + chestCount + "+" + inventoryCount + "+" + carriedCount + " != " + expected);
            List<ItemStack> after = snapshot(players);
            boolean rejected = !List.of("concurrent", "forged_count", "player_identity").contains(PHASES[phase]);
            if (rejected && (chestCount != 32 || inventoryCount != 0 || carriedCount != 0 || !matches(before, after))) throw new AssertionError("Rejected action mutated exact slot/component state");
            if (PHASES[phase].equals("position") && players.stream().anyMatch(p -> p.containerMenu != p.inventoryMenu)) throw new AssertionError("Out-of-range position opened a menu");
            if (PHASES[phase].equals("concurrent") && (chestCount != 0 || inventoryCount != 32)) throw new AssertionError("Positive simultaneous transfer did not execute");
            if (PHASES[phase].equals("player_identity") && (players.get(0).getInventory().countItem(Items.EMERALD) != 0 || players.get(1).getInventory().countItem(Items.DIAMOND) != 0 || inventoryCount != 49)) throw new AssertionError("Player/menu association violated");
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("phase", PHASES[phase]); row.put("received", received); row.put("chest", chestCount); row.put("inventory", inventoryCount); row.put("carried", carriedCount); row.put("serverThread", Thread.currentThread().getName());
            row.put("exactSlotsUnchanged", matches(before, after));
            row.put("beforeSlots", before.stream().map(ItemStack::toString).toList());
            row.put("afterSlots", after.stream().map(ItemStack::toString).toList());
            row.put("players", players.stream().map(p -> Map.of("name", p.getGameProfile().name(), "uuid", p.getUUID().toString(), "menu", p.containerMenu.containerId, "menuIdentity", System.identityHashCode(p.containerMenu))).toList());
            results.add(row);
            append("server-events.log", "PASS " + new GsonBuilder().create().toJson(row));
            phase++;
            if (phase == PHASES.length) {
                finished = true;
                Files.writeString(directory.resolve("result.json"), new GsonBuilder().setPrettyPrinting().create().toJson(Map.of("passed", true, "phases", results)));
                Files.writeString(directory.resolve("done"), "passed");
                server.halt(false);
            } else setup(server, players);
        } catch (Throwable failure) {
            finished = true;
            append("server-events.log", "FAIL " + failure);
            try { Files.writeString(directory.resolve("failure.txt"), failure.toString()); } catch (Exception ignored) { }
            server.halt(false);
            throw new AssertionError("Network fixture failed", failure);
        }
    }

    private static void setup(MinecraftServer server, List<ServerPlayer> players) throws Exception {
        var level = server.overworld();
        for (ServerPlayer player : players) {
            player.closeContainer();
            player.setGameMode(GameType.SURVIVAL);
            player.getInventory().clearContent();
            player.getAbilities().mayfly = true;
            player.getAbilities().flying = true;
            player.onUpdateAbilities();
            player.connection.teleport(0.5, 66, 2.5, 180, 0);
        }
        level.getEntitiesOfClass(ItemEntity.class, new net.minecraft.world.phys.AABB(-16, 0, -16, 16, 100, 16)).forEach(ItemEntity::discard);
        chests.clear();
        for (int i = 0; i < (PHASES[phase].equals("player_identity") ? 2 : 1); i++) {
            BlockPos pos = new BlockPos(i * 3, 65, 0);
            if (level.getBlockEntity(pos) instanceof ChestBlockEntity previous) previous.clearContent();
            level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            level.setBlockAndUpdate(pos.below(), Blocks.STONE.defaultBlockState());
            level.setBlockAndUpdate(pos, Blocks.CHEST.defaultBlockState());
            ChestBlockEntity chest = (ChestBlockEntity) level.getBlockEntity(pos);
            chest.setItem(0, new ItemStack(i == 0 ? Items.DIAMOND : Items.EMERALD, i == 0 ? 32 : 17));
            chests.add(chest);
        }
        Properties control = new Properties();
        control.setProperty("phase", Integer.toString(phase));
        control.setProperty("action", PHASES[phase]);
        for (int i = 0; i < players.size(); i++) {
            ServerPlayer player = players.get(i);
            var chest = chests.get(PHASES[phase].equals("player_identity") ? i : 0);
            open(player, chest);
            int id = player.containerMenu.containerId;
            if (PHASES[phase].equals("stale_menu")) { player.closeContainer(); open(player, chests.get(0)); }
            if (PHASES[phase].equals("distance") || PHASES[phase].equals("position")) player.connection.teleport(100.5, 66, 100.5, 0, 0);
            if (PHASES[phase].equals("spectator")) player.setGameMode(GameType.SPECTATOR);
            control.setProperty("id." + player.getGameProfile().name(), Integer.toString(PHASES[phase].equals("wrong_id") ? id + 100 : id));
        }
        if (PHASES[phase].equals("player_identity") && players.get(0).containerMenu.containerId != players.get(1).containerMenu.containerId) throw new AssertionError("Identity fixture needs equal numeric menu IDs");
        if (PHASES[phase].equals("removed")) level.removeBlockEntity(chests.get(0).getBlockPos());
        before = snapshot(players);
        received = 0;
        started = server.getTickCount();
        Path temp = directory.resolve("phase.tmp");
        try (var writer = Files.newBufferedWriter(temp)) { control.store(writer, "actual client orchestration; not a network message"); }
        Files.move(temp, directory.resolve("phase.properties"), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        append("server-events.log", "SETUP phase=" + phase + " " + PHASES[phase] + " players=" + players.stream().map(p -> p.getGameProfile().name() + ":" + p.getUUID() + ":menu=" + p.containerMenu.containerId).toList());
    }

    public static Path directory() { return directory; }
    private static List<ItemStack> snapshot(List<ServerPlayer> players) {
        List<ItemStack> stacks = new ArrayList<>();
        for (var chest : chests) for (int slot = 0; slot < chest.getContainerSize(); slot++) stacks.add(chest.getItem(slot).copy());
        for (var player : players) {
            for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) stacks.add(player.getInventory().getItem(slot).copy());
            stacks.add(player.containerMenu.getCarried().copy());
        }
        return stacks;
    }
    private static boolean matches(List<ItemStack> left, List<ItemStack> right) {
        if (left.size() != right.size()) return false;
        for (int slot = 0; slot < left.size(); slot++) if (!ItemStack.matches(left.get(slot), right.get(slot))) return false;
        return true;
    }
    private static void open(ServerPlayer player, ChestBlockEntity chest) {
        site.siredvin.broccolium.modules.platform.PlatformToolkit.INSTANCE.get().openMenu(player, new net.minecraft.world.MenuProvider() {
            @Override public net.minecraft.network.chat.Component getDisplayName() { return net.minecraft.network.chat.Component.literal("R6 network fixture"); }
            @Override public AbstractContainerMenu createMenu(int id, net.minecraft.world.entity.player.Inventory inventory, net.minecraft.world.entity.player.Player owner) {
                return new net.minecraft.world.inventory.ChestMenu(NetworkMenus.type.get(), id, inventory, chest, 3);
            }
        }, buffer -> { buffer.writeBlockPos(new BlockPos(0, 65, 0)); buffer.writeInt(27); });
    }
    public static void append(String file, String line) {
        try { Files.writeString(directory.resolve(file), line + "\n", StandardOpenOption.CREATE, StandardOpenOption.APPEND); }
        catch (Exception exception) { throw new IllegalStateException(exception); }
    }
}
