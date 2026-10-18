package site.siredvin.tweakium.clienttest;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.renderer.item.TrackingItemStackRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/** Real-client visual workflow. All mutable game access occurs on client extraction/tick, never submission. */
public final class ClientProof {
    private static int phase;
    private static int wait;
    private static int frames;
    private static boolean captured = true;
    private static CompletableFuture<Void> reload;
    private static List<ItemStack> gallery = List.of();
    private static String route;

    public static void tick() {
        if (!ClientFixture.enabled()) return;
        var mc = Minecraft.getInstance();
        if (!mc.isSameThread()) throw new AssertionError("Wrong client thread");
        if (!captured || mc.getOverlay() != null || ++wait < 40) return;
        try {
            if (!InteractionProof.advance(mc, phase)) return;
            switch (phase) {
                case 0 -> {
                    if (!(mc.screen instanceof TitleScreen) || !mc.isGameLoadFinished()) return;
                    route = System.getProperty("tweakium.client.route");
                    shot(mc, "01-title");
                }
                case 1 -> mc.createWorldOpenFlows().createFreshLevel("r7-client",
                    new LevelSettings("R7 Visual", GameType.CREATIVE, new LevelSettings.DifficultySettings(Difficulty.EASY, false, false), true, WorldDataConfiguration.DEFAULT),
                    new WorldOptions(0, false, false), lookup -> lookup.lookupOrThrow(Registries.WORLD_PRESET).getOrThrow(WorldPresets.FLAT).value().createWorldDimensions(), new TitleScreen());
                case 2 -> {
                    if (!ready(mc) || !mc.hasSingleplayerServer()) return;
                    verify(mc);
                    shot(mc, "02-integrated-world");
                }
                case 3 -> mc.setScreen(new Gallery());
                case 4 -> { if (frames < 10) return; shot(mc, "03-content-models-tooltip"); }
                case 5 -> {
                    mc.setScreen(new CreativeModeInventoryScreen(mc.player, mc.level.enabledFeatures(), true));
                    var tab = CreativeModeTabs.tabs().stream().filter(t -> t.getDisplayName().getString().contains("Peripheralium")).findFirst().orElseThrow();
                    var method = CreativeModeInventoryScreen.class.getDeclaredMethod("selectTab", net.minecraft.world.item.CreativeModeTab.class);
                    method.setAccessible(true);
                    method.invoke(mc.screen, tab);
                    if (tab.getDisplayItems().size() != 3) throw new AssertionError("Creative content count " + tab.getDisplayItems().size());
                }
                case 6 -> shot(mc, "04-creative-tab");
                case 7 -> mc.setScreen(new BookViewScreen(BookViewScreen.BookAccess.fromItem(ClientFixture.book())));
                case 8 -> shot(mc, "05-book-page-one");
                case 9 -> { if (!((BookViewScreen) mc.screen).setPage(1)) throw new AssertionError("Book page interaction"); }
                case 10 -> shot(mc, "06-book-page-two");
                case 11 -> { mc.setScreen(null); reload = mc.reloadResourcePacks(); }
                case 12 -> {
                    if (!reload.isDone()) return;
                    reload.join(); verify(mc); mc.setScreen(new Gallery());
                }
                case 13 -> { if (frames < 10) return; shot(mc, "07-reloaded-models"); }
                case 14 -> { mc.disconnectFromWorld(Component.literal("R7 integrated complete")); mc.setScreen(new TitleScreen()); }
                case 15 -> connect(mc);
                case 16 -> { if (!ready(mc) || mc.hasSingleplayerServer()) return; verify(mc); shot(mc, "08-dedicated-join"); }
                case 17 -> {
                    mc.gameMode.useItemOn(mc.player, InteractionHand.MAIN_HAND, new BlockHitResult(new Vec3(3.5, 65.5, 3.0), Direction.NORTH, new BlockPos(3, 65, 3), false));
                }
                case 18 -> { if (!(mc.screen instanceof AbstractContainerScreen<?>)) return; shot(mc, "09-native-menu"); }
                case 19 -> { mc.player.closeContainer(); mc.player.getInventory().setSelectedSlot(5); mc.gameMode.useItem(mc.player, InteractionHand.MAIN_HAND); }
                case 20 -> {
                    if (!(mc.screen instanceof dan200.computercraft.client.gui.AbstractComputerScreen<?>)) return;
                    shot(mc, "10-equipped-pocket-menu");
                }
                case 21 -> { mc.player.closeContainer(); mc.disconnectFromWorld(Component.literal("R7 reconnect")); mc.setScreen(new TitleScreen()); }
                case 22 -> connect(mc);
                case 23 -> { if (!ready(mc)) return; verify(mc); shot(mc, "11-dedicated-reconnect"); }
                case 24 -> { mc.setScreen(new Gallery()); }
                case 25 -> { if (frames < 10) return; shot(mc, "12-reconnected-models"); }
                case 26 -> {
                    Files.writeString(ClientFixture.directory().resolve("result.json"), "{\"passed\":true,\"route\":\"" + route + "\",\"phases\":26,\"screenshots\":12,\"thread\":\"" + Thread.currentThread().getName() + "\"}\n");
                    mc.disconnectFromWorld(Component.literal("R7 complete")); mc.stop();
                }
                default -> { return; }
            }
            ClientFixture.record("client.log", "phase=" + phase + " screen=" + (mc.screen == null ? "world" : mc.screen.getClass().getName()) + " thread=" + Thread.currentThread().getName());
            phase++; wait = 0;
        } catch (Throwable failure) {
            ClientFixture.record("failure.txt", "phase=" + phase + " " + failure);
            throw new AssertionError("R7 actual client phase=" + phase, failure);
        }
    }
    private static boolean ready(Minecraft mc) {
        if (mc.screen != null) { wait = 0; return false; }
        return mc.player != null && mc.level != null && mc.getConnection() != null && mc.player.getInventory().getItem(6).is(net.minecraft.world.item.Items.WRITTEN_BOOK);
    }
    private static void connect(Minecraft mc) {
        String address = System.getProperty("tweakium.client.address");
        ConnectScreen.startConnecting(new TitleScreen(), mc, ServerAddress.parseString(address), new ServerData("R7 fixture", address, ServerData.Type.OTHER), false, null);
    }
    private static void shot(Minecraft mc, String name) {
        captured = false;
        Screenshot.grab(ClientFixture.directory().toFile(), name + ".png", mc.getMainRenderTarget(), 1, ignored -> captured = true);
    }
    private static void verify(Minecraft mc) {
        CreativeModeTabs.tryRebuildTabContents(mc.level.enabledFeatures(), true, mc.level.registryAccess());
        gallery = java.util.stream.IntStream.range(0, 7).mapToObj(i -> mc.player.getInventory().getItem(i).copy()).toList();
        var tooltip = gallery.get(0).getTooltipLines(net.minecraft.world.item.Item.TooltipContext.of(mc.level), mc.player, net.minecraft.world.item.TooltipFlag.NORMAL);
        if (tooltip.stream().noneMatch(line -> line.getString().equals("R7 consumer description via Broccolium"))) throw new AssertionError("Native descriptive tooltip hook");
        for (int i = 0; i < gallery.size(); i++) {
            var stack = gallery.get(i);
            if (stack.isEmpty()) throw new AssertionError("Missing synchronized fixture slot " + i);
            var state = new TrackingItemStackRenderState();
            mc.getItemModelResolver().updateForTopItem(state, stack, ItemDisplayContext.GUI, mc.level, mc.player, 0);
            if (state.isEmpty()) throw new AssertionError("Empty rendered model " + stack);
            ClientFixture.record("models.log", "route=" + route + " phase=" + phase + " item=" + BuiltInRegistries.ITEM.getKey(stack.getItem()) + " identity=" + state.getModelIdentity() + " bounds=" + state.getModelBoundingBox());
        }
        // Model registration is a real resource decode, not just constructing the class.
        var resource = mc.getResourceManager().getResource(net.minecraft.resources.Identifier.fromNamespaceAndPath("tweakium", "computercraft/turtle_upgrade/r7_visual.json")).orElseThrow();
        try (var reader = resource.openAsReader()) {
            var json = com.google.gson.JsonParser.parseReader(reader);
            var model = dan200.computercraft.api.client.turtle.TurtleUpgradeModel.CODEC.parse(com.mojang.serialization.JsonOps.INSTANCE, json).getOrThrow();
            if (model.type() != site.siredvin.tweakium.modules.turtle.client.FacingBlockTurtleModeller.Companion.getCODEC()) throw new AssertionError("Wrong model codec");
        } catch (java.io.IOException exception) { throw new AssertionError(exception); }
        var left = gallery.get(3).get(dan200.computercraft.shared.ModRegistry.DataComponents.LEFT_TURTLE_UPGRADE.get());
        var right = gallery.get(4).get(dan200.computercraft.shared.ModRegistry.DataComponents.RIGHT_TURTLE_UPGRADE.get());
        var pocket = gallery.get(5).get(dan200.computercraft.shared.ModRegistry.DataComponents.BACK_POCKET_UPGRADE.get());
        if (left == null || right == null || pocket == null || !left.holder().key().identifier().equals(ClientFixture.ID) || !right.holder().key().identifier().equals(ClientFixture.ID)) throw new AssertionError("Equipped upgrade synchronization");
        RenderProof.verify(mc, gallery, route + ":" + phase);
        InteractionProof.verify(mc);
    }
    private static final class Gallery extends Screen {
        Gallery() { super(Component.literal("R7 content and upgrade models")); frames = 0; }
        @Override public boolean isPauseScreen() { return false; }
        @Override public void extractRenderState(GuiGraphicsExtractor gui, int mouseX, int mouseY, float delta) {
            gui.fill(0, 0, width, height, 0xff17232d);
            gui.text(font, "R7 " + route + " — extracted production models", 10, 10, 0xffeeeeee);
            for (int i = 0; i < gallery.size(); i++) {
                var stack = gallery.get(i);
                int x = 16 + i * 42;
                gui.pose().pushMatrix(); gui.pose().translate(x, 45); gui.pose().scale(2); gui.item(stack, 0, 0); gui.pose().popMatrix();
                gui.text(font, Integer.toString(i), x + 10, 82, 0xffaaddee);
            }
            gui.text(font, "Both facing turtle sides; equipped pocket; two-page book", 10, 105, 0xffeeeeee);
            gui.setTooltipForNextFrame(font, gallery.get(0), 35, 125);
            frames++;
        }
    }
}
