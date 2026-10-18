package site.siredvin.tweakium.clienttest;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import site.siredvin.broccolium.modules.platform.PlatformToolkit;
import site.siredvin.tweakium.modules.platform.api.InnerComputerBasePlatform;
import java.nio.file.Files;
import java.util.function.Supplier;

/** Private consumer of the real Broccolium extended-menu API, not a production protocol. */
public final class InteractionMenus {
    public static final BlockPos CHEST = new BlockPos(3, 65, 3);
    public static Supplier<MenuType<ChestMenu>> type;
    private static String handled = "";
    private static boolean acknowledged;

    public static void register(InnerComputerBasePlatform platform) {
        type = platform.registerMenu(Identifier.fromNamespaceAndPath("tweakium", "r7_interaction_menu"), (id, inventory, data) -> {
            if (!(data instanceof RegistryFriendlyByteBuf registry) || registry.registryAccess() != inventory.player.registryAccess()) throw new AssertionError("Menu lost registry context");
            if (data.readableBytes() != 12 || !data.readBlockPos().equals(CHEST) || data.readInt() != 27) throw new AssertionError("Unexpected consumer opening data");
            ClientFixture.record("interactions.log", "menu-decode registry-context=true schema=BlockPos,Int thread=" + Thread.currentThread().getName());
            return new ChestMenu(type.get(), id, inventory, new SimpleContainer(27), 3);
        });
    }

    public static void tick(MinecraftServer server) {
        if (!server.isDedicatedServer()) return;
        if (!server.isSameThread()) throw new AssertionError("Interaction fixture not on server thread");
        for (var player : server.getPlayerList().getPlayers()) {
            if (!(server.overworld().getBlockEntity(CHEST) instanceof ChestBlockEntity chest)) continue;
            try {
                var request = ClientFixture.directory().resolve("interaction-menu.request");
                if (Files.exists(request)) {
                    String token = Files.readString(request);
                    if (!token.isBlank() && !token.equals(handled)) {
                        PlatformToolkit.INSTANCE.get().openMenu(player, new net.minecraft.world.MenuProvider() {
                            @Override public Component getDisplayName() { return Component.literal("R7 Broccolium interaction menu"); }
                            @Override public ChestMenu createMenu(int id, net.minecraft.world.entity.player.Inventory inventory, net.minecraft.world.entity.player.Player owner) {
                                return new ChestMenu(type.get(), id, inventory, chest, 3);
                            }
                        }, data -> { data.writeBlockPos(CHEST); data.writeInt(27); });
                        handled = token;
                        ClientFixture.record("interactions.log", "server-open token=" + token + " id=" + player.containerMenu.containerId + " thread=" + Thread.currentThread().getName());
                    }
                }
                if (!acknowledged && chest.getItem(0).isEmpty() && chest.getItem(1).is(Items.EMERALD) && chest.getItem(1).getCount() == 7 && player.getInventory().countItem(Items.DIAMOND) == 16) {
                    Files.writeString(ClientFixture.directory().resolve("interaction-transfer.ack"), "diamond=16 emerald=7 chest-slot0=empty server-thread=" + Thread.currentThread().getName());
                    acknowledged = true;
                    ClientFixture.record("interactions.log", "server-ack exact-conservation diamond=16 emerald=7");
                }
            } catch (java.io.IOException exception) { throw new AssertionError(exception); }
        }
    }
}
