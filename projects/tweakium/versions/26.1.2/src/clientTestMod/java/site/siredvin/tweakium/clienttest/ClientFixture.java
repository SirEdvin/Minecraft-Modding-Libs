package site.siredvin.tweakium.clienttest;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.pocket.AbstractPocketUpgrade;
import dan200.computercraft.api.pocket.IPocketAccess;
import dan200.computercraft.api.pocket.IPocketUpgrade;
import dan200.computercraft.api.turtle.AbstractTurtleUpgrade;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.api.turtle.TurtleUpgradeType;
import dan200.computercraft.api.upgrades.UpgradeData;
import dan200.computercraft.api.upgrades.UpgradeType;
import dan200.computercraft.shared.ModRegistry;
import dan200.computercraft.shared.turtle.blocks.TurtleBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import site.siredvin.tweakium.modules.platform.api.InnerComputerBasePlatform;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Private consumer definitions. Production libraries provide extension APIs, not concrete upgrades. */
public final class ClientFixture {
    public static final Identifier ID = Identifier.fromNamespaceAndPath("tweakium", "r7_visual");
    private static final UpgradeType<Turtle> TURTLE = UpgradeType.simpleWithCustomItem(Turtle::new);
    private static final UpgradeType<Pocket> POCKET = UpgradeType.simpleWithCustomItem(Pocket::new);
    private static final Set<String> prepared = new HashSet<>();

    public static Path directory() { return Path.of(System.getProperty("tweakium.client.dir")); }
    public static boolean enabled() { return System.getProperty("tweakium.client.dir") != null; }
    public static void record(String file, String text) {
        try { Files.writeString(directory().resolve(file), text + "\n", StandardOpenOption.CREATE, StandardOpenOption.APPEND); }
        catch (java.io.IOException exception) { throw new AssertionError(exception); }
    }
    public static void register(InnerComputerBasePlatform platform) {
        platform.registerTurtleUpgrade(ID, TURTLE);
        platform.registerPocketUpgrade(ID, POCKET);
    }
    public static final class Turtle extends AbstractTurtleUpgrade {
        public Turtle(ItemStackTemplate item) { super(TurtleUpgradeType.PERIPHERAL, Component.literal("R7 Facing Block"), item); }
        @Override public UpgradeType<? extends ITurtleUpgrade> getType() { return TURTLE; }
        @Override public IPeripheral createPeripheral(ITurtleAccess turtle, TurtleSide side) { return null; }
    }
    public static final class Pocket extends AbstractPocketUpgrade {
        public Pocket(ItemStackTemplate item) { super(Component.literal("R7 Pocket"), item); }
        @Override public UpgradeType<? extends IPocketUpgrade> getType() { return POCKET; }
        @Override public IPeripheral createPeripheral(IPocketAccess pocket) { return null; }
    }

    public static ItemStack book() {
        ItemStack stack = new ItemStack(Items.WRITTEN_BOOK);
        stack.set(DataComponents.WRITTEN_BOOK_CONTENT, new WrittenBookContent(Filterable.passThrough("R7 field notes"), "Testiarium", 0,
            List.of(Filterable.passThrough(Component.literal("Broccolium book text\nPeripheralium content\nTweakium facing models")), Filterable.passThrough(Component.literal("Second page: resource reload and reconnect."))), true));
        if (site.siredvin.broccolium.modules.base.util.TextBookUtils.INSTANCE.getBookText(stack).size() != 2) throw new AssertionError("Book text contract");
        return stack;
    }

    public static void tick(MinecraftServer server) {
        if (!enabled()) return;
        for (var player : server.getPlayerList().getPlayers()) {
            if (!prepared.add(System.identityHashCode(server) + ":" + player.getUUID())) continue;
            var level = server.overworld();
            player.setGameMode(GameType.CREATIVE);
            for (int x = -6; x <= 6; x++) for (int z = -3; z <= 8; z++) {
                level.setBlockAndUpdate(new BlockPos(x, 64, z), Blocks.SMOOTH_STONE.defaultBlockState());
                for (int y = 65; y <= 69; y++) level.setBlockAndUpdate(new BlockPos(x, y, z), Blocks.AIR.defaultBlockState());
            }
            var holder = level.registryAccess().lookupOrThrow(ITurtleUpgrade.REGISTRY).getOrThrow(ResourceKey.create(ITurtleUpgrade.REGISTRY, ID));
            var pocketHolder = level.registryAccess().lookupOrThrow(IPocketUpgrade.REGISTRY).getOrThrow(ResourceKey.create(IPocketUpgrade.REGISTRY, ID));
            player.getInventory().clearContent();
            int slot = 0;
            for (var id : List.of("peripheralium_dust", "peripheralium_upgrade_template", "peripheralium_block")) {
                var item = BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath("peripheralium", id));
                player.getInventory().setItem(slot++, new ItemStack(item));
            }
            for (var side : TurtleSide.values()) {
                ItemStack stack = new ItemStack(ModRegistry.Items.TURTLE_ADVANCED.get());
                stack.set(side == TurtleSide.LEFT ? ModRegistry.DataComponents.LEFT_TURTLE_UPGRADE.get() : ModRegistry.DataComponents.RIGHT_TURTLE_UPGRADE.get(), UpgradeData.ofDefault(holder));
                player.getInventory().setItem(slot++, stack);
                BlockPos pos = new BlockPos(side == TurtleSide.LEFT ? -1 : 1, 65, 3);
                level.setBlockAndUpdate(pos, ModRegistry.Blocks.TURTLE_ADVANCED.get().defaultBlockState());
                var turtle = ((TurtleBlockEntity) level.getBlockEntity(pos)).getAccess();
                turtle.setUpgrade(side, UpgradeData.ofDefault(holder));
                site.siredvin.tweakium.modules.peripheral.util.DataStorageUtil.INSTANCE.getDataStorage(turtle, side).putString("ownerProfile", "R7-" + side);
            }
            ItemStack pocket = new ItemStack(ModRegistry.Items.POCKET_COMPUTER_ADVANCED.get());
            pocket.set(ModRegistry.DataComponents.BACK_POCKET_UPGRADE.get(), UpgradeData.of(pocketHolder, DataComponentPatch.builder().set(DataComponents.CUSTOM_NAME, Component.literal("R7 state")).build()));
            player.getInventory().setItem(5, pocket);
            player.getInventory().setItem(6, book());
            level.setBlockAndUpdate(new BlockPos(0, 65, 3), BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath("peripheralium", "peripheralium_block")).defaultBlockState());
            level.setBlockAndUpdate(new BlockPos(3, 65, 3), Blocks.CHEST.defaultBlockState());
            player.connection.teleport(1.5, 66, 0.5, 0, 14);
            player.getInventory().setChanged();
            player.containerMenu.broadcastChanges();
            record("server.log", "prepared player=" + player.getUUID() + " dedicated=" + server.isDedicatedServer() + " thread=" + Thread.currentThread().getName());
        }
    }
}
