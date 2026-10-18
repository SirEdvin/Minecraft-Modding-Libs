package site.siredvin.tweakium.clienttest;

import com.google.gson.GsonBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import site.siredvin.broccolium.modules.base.util.TextBookUtils;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/** Assertions layered on the rendering-owned workflow without changing its phase/evidence schema. */
public final class InteractionProof {
    private static final Set<String> NAMESPACES = Set.of("broccolium", "tweakium", "peripheralium", "testiarium");
    private static final Set<String> ITEMS = Set.of("peripheralium:peripheralium_block", "peripheralium:peripheralium_dust", "peripheralium:peripheralium_upgrade_template", "tweakium:creative_filler");
    private static final Set<String> BLOCKS = Set.of("peripheralium:peripheralium_block", "tweakium:creative_filler");
    private static int step;
    private static int ticks;
    private static int firstMenu;
    private static volatile boolean captured = true;
    private static int stable;
    private static boolean screenRegistered;

    public static void verify(Minecraft mc) {
        registerScreen();
        Set<String> items = new TreeSet<>();
        Set<String> blocks = new TreeSet<>();
        BuiltInRegistries.ITEM.keySet().stream().filter(id -> NAMESPACES.contains(id.getNamespace()) && !id.equals(ClientFixture.HIDDEN_PROBE)).forEach(id -> items.add(id.toString()));
        BuiltInRegistries.BLOCK.keySet().stream().filter(id -> NAMESPACES.contains(id.getNamespace())).forEach(id -> blocks.add(id.toString()));
        require(items.equals(ITEMS), "Exact production item inventory " + items);
        require(blocks.equals(BLOCKS), "Exact production block inventory " + blocks);
        var tab = BuiltInRegistries.CREATIVE_MODE_TAB.getValue(Identifier.fromNamespaceAndPath("peripheralium", "tab"));
        require(tab != null, "Peripheralium creative tab");
        List<String> display = tab.getDisplayItems().stream().map(stack -> BuiltInRegistries.ITEM.getKey(stack.getItem()).toString()).toList();
        require(display.size() == 3 && Set.copyOf(display).equals(Set.of("peripheralium:peripheralium_dust", "peripheralium:peripheralium_upgrade_template", "peripheralium:peripheralium_block")), "Exact creative entries " + display);
        var upgradeCreative = new java.util.ArrayList<String>();
        site.siredvin.tweakium.modules.peripheral.util.CreativeTabUtil.INSTANCE.enrichCreativeTabWithUpgrades("tweakium", (stack, visibility) -> {
            var turtle = stack.get(dan200.computercraft.shared.ModRegistry.DataComponents.RIGHT_TURTLE_UPGRADE.get());
            var pocket = stack.get(dan200.computercraft.shared.ModRegistry.DataComponents.BACK_POCKET_UPGRADE.get());
            var upgrade = turtle != null ? turtle : pocket;
            upgradeCreative.add(BuiltInRegistries.ITEM.getKey(stack.getItem()) + "|" + (upgrade == null ? "base" : upgrade.holder().key().identifier().toString()));
        }, mc.level.registryAccess());
        var expectedUpgrades = new java.util.ArrayList<String>();
        for (String item : List.of("pocket_computer_normal", "pocket_computer_advanced", "turtle_normal", "turtle_advanced")) {
            expectedUpgrades.add("computercraft:" + item + "|base");
            expectedUpgrades.add("computercraft:" + item + "|tweakium:r7_visual");
        }
        require(upgradeCreative.equals(expectedUpgrades), "Exact consumer upgrade creative entries " + upgradeCreative);
        ItemStack book = mc.player.getInventory().getItem(6);
        require(TextBookUtils.INSTANCE.getBookText(book).equals(ClientFixture.BOOK_PAGES), "Synchronized written book exact pages/order");
        var content = book.get(DataComponents.WRITTEN_BOOK_CONTENT);
        require(content != null && content.title().raw().equals("R7 field notes") && content.author().equals("Testiarium"), "Synchronized book metadata");
        ItemStack dust = mc.player.getInventory().getItem(0);
        var tooltip = dust.getTooltipLines(Item.TooltipContext.of(mc.level), mc.player, TooltipFlag.NORMAL);
        require(tooltip.stream().filter(line -> line.getString().equals("R7 consumer description via Broccolium")).count() == 1, "Native descriptive tooltip exactly once");
        var hidden = ClientFixture.hiddenProbe.get();
        var prompt = new ItemStack(hidden).getTooltipLines(Item.TooltipContext.of(mc.level), mc.player, TooltipFlag.NORMAL);
        require(prompt.stream().anyMatch(line -> line.getString().contains("Left shift")), "Native hidden tooltip prompt");
        require(prompt.stream().noneMatch(line -> line.getString().equals("private hook")), "Hidden hook leaked without Left Shift");
        try {
            Files.writeString(ClientFixture.directory().resolve("interaction-inventory.json"), new GsonBuilder().setPrettyPrinting().create().toJson(Map.of("items", items, "blocks", blocks, "peripheraliumCreativeDisplay", display, "consumerUpgradeCreative", upgradeCreative, "bookPages", ClientFixture.BOOK_PAGES, "privateConsumers", List.of("tweakium:r7_visual turtle left/right", "tweakium:r7_visual pocket", "tweakium:r7_interaction_menu", ClientFixture.HIDDEN_PROBE.toString()))) + "\n");
        } catch (java.io.IOException exception) { throw new AssertionError(exception); }
        ClientFixture.record("interactions.log", "verify dedicated=" + !mc.hasSingleplayerServer() + " exact-items=4 exact-blocks=2 creative=3 book-pages=2 descriptive-tooltip=1 hidden-prompt=true thread=" + Thread.currentThread().getName());
    }

    /** Return false while a real menu operation is awaiting server acknowledgement. */
    public static boolean advance(Minecraft mc, int phase) {
        if (phase == 6) {
            require(mc.screen instanceof net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen, "Visible creative screen");
            var screen = (net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen) mc.screen;
            var visible = screen.getMenu().items.stream().map(stack -> BuiltInRegistries.ITEM.getKey(stack.getItem()).toString()).toList();
            require(visible.size() == 3 && Set.copyOf(visible).equals(Set.of("peripheralium:peripheralium_dust", "peripheralium:peripheralium_upgrade_template", "peripheralium:peripheralium_block")), "Visible creative inventory changed before screenshot: " + visible);
            try {
                var field = screen.getClass().getDeclaredField("selectedTab");
                field.setAccessible(true);
                require(field.get(null) == BuiltInRegistries.CREATIVE_MODE_TAB.getValue(Identifier.fromNamespaceAndPath("peripheralium", "tab")), "Visible creative label/tab changed");
            } catch (ReflectiveOperationException exception) { throw new AssertionError(exception); }
            ClientFixture.record("interactions.log", "PASS visible-creative-tab peripheralium:tab exact-display=3");
            return true;
        }
        if (phase != 19 && phase != 24) return true;
        if (++ticks > 600) throw new AssertionError("Interaction deadline phase=" + phase + " step=" + step);
        if (!captured) return false;
        try {
            if (phase == 19) {
                switch (step) {
                    case 0 -> {
                        if (!(mc.player.containerMenu instanceof ChestMenu menu)) return false;
                        require(menu.getSlot(0).getItem().is(Items.DIAMOND) && menu.getSlot(0).getItem().getCount() == 16, "Vanilla chest synchronized diamond seed");
                        mc.player.closeContainer();
                        step = 1; ticks = 0;
                    }
                    case 1 -> {
                        if (ticks < 10) return false;
                        Files.writeString(ClientFixture.directory().resolve("interaction-menu.request"), "initial");
                        step = 2;
                    }
                    case 2 -> {
                        if (!(mc.player.containerMenu instanceof ChestMenu menu) || menu.getType() != InteractionMenus.type.get() || !menu.getSlot(0).getItem().is(Items.DIAMOND)) return false;
                        if (++stable < 5) return false;
                        require(menu.getSlot(0).getItem().getCount() == 16, "Broccolium menu initial diamond count");
                        checkEmerald(menu);
                        firstMenu = menu.containerId;
                        shot(mc, "01-extended-menu");
                        stable = 0;
                        step = 3;
                    }
                    case 3 -> {
                        mc.gameMode.handleContainerInput(firstMenu, 0, 0, ContainerInput.QUICK_MOVE, mc.player);
                        step = 4;
                    }
                    case 4 -> {
                        if (!Files.exists(ClientFixture.directory().resolve("interaction-transfer.ack"))) return false;
                        if (!(mc.player.containerMenu instanceof ChestMenu menu) || !menu.getSlot(0).getItem().isEmpty() || mc.player.getInventory().countItem(Items.DIAMOND) != 16) return false;
                        checkEmerald(menu);
                        require(menu.getCarried().isEmpty(), "No carried remainder");
                        mc.player.closeContainer();
                        step = 5; ticks = 0;
                    }
                    case 5 -> {
                        if (ticks < 10) return false;
                        Files.writeString(ClientFixture.directory().resolve("interaction-menu.request"), "reopen");
                        step = 6;
                    }
                    case 6 -> {
                        if (!mutated(mc)) return false;
                        if (++stable < 5) return false;
                        require(mc.player.containerMenu.containerId != firstMenu, "Reopen must create a fresh menu id");
                        shot(mc, "02-reopened-menu");
                        stable = 0;
                        ClientFixture.record("interactions.log", "PASS extended-open transfer-server-ack exact-conservation close-reopen fresh-menu-id");
                        step = 7;
                    }
                    case 7 -> { ticks = 0; return true; }
                    default -> throw new AssertionError("Invalid interaction stage " + step);
                }
            } else {
                switch (step) {
                    case 7 -> {
                        require(mc.player.getInventory().countItem(Items.DIAMOND) == 16, "Reconnect player inventory conservation");
                        Files.writeString(ClientFixture.directory().resolve("interaction-menu.request"), "reconnect");
                        step = 8;
                    }
                    case 8 -> {
                        if (!mutated(mc)) return false;
                        if (++stable < 5) return false;
                        shot(mc, "03-reconnected-menu");
                        ClientFixture.record("interactions.log", "PASS reconnect authoritative-chest-and-player-state");
                        step = 9;
                    }
                    case 9 -> { mc.player.closeContainer(); return true; }
                    default -> throw new AssertionError("Reconnect before menu regression completed");
                }
            }
            return false;
        } catch (java.io.IOException exception) { throw new AssertionError(exception); }
    }

    private static boolean mutated(Minecraft mc) {
        if (!(mc.player.containerMenu instanceof ChestMenu menu) || menu.getType() != InteractionMenus.type.get() || !menu.getSlot(1).getItem().is(Items.EMERALD)) return false;
        require(menu.getSlot(0).getItem().isEmpty() && mc.player.getInventory().countItem(Items.DIAMOND) == 16, "Removed diamonds reappeared or disappeared");
        checkEmerald(menu);
        return true;
    }
    private static void checkEmerald(ChestMenu menu) { require(menu.getSlot(1).getItem().is(Items.EMERALD) && menu.getSlot(1).getItem().getCount() == 7, "Untouched emerald count"); }
    private static void require(boolean condition, String message) { if (!condition) throw new AssertionError(message); }
    private static void shot(Minecraft mc, String name) {
        var directory = ClientFixture.directory().resolve("interaction");
        try { Files.createDirectories(directory.resolve("screenshots")); }
        catch (java.io.IOException exception) { throw new AssertionError(exception); }
        captured = false;
        Screenshot.grab(directory.toFile(), name + ".png", mc.getMainRenderTarget(), 1, ignored -> {
            if (!Files.isRegularFile(directory.resolve("screenshots").resolve(name + ".png"))) ClientFixture.record("failure.txt", "Interaction screenshot failed: " + name);
            captured = true;
        });
    }
    // Same isolated private vanilla registration boundary as the existing R6 fixture.
    private static void registerScreen() {
        if (screenRegistered) return;
        try {
            var constructor = Class.forName("net.minecraft.client.gui.screens.MenuScreens$ScreenConstructor");
            var factory = java.lang.reflect.Proxy.newProxyInstance(constructor.getClassLoader(), new Class<?>[]{constructor}, (proxy, method, arguments) -> {
                if (method.getName().equals("create")) return new net.minecraft.client.gui.screens.inventory.ContainerScreen((ChestMenu) arguments[0], (net.minecraft.world.entity.player.Inventory) arguments[1], (Component) arguments[2]);
                throw new UnsupportedOperationException(method.toString());
            });
            var register = net.minecraft.client.gui.screens.MenuScreens.class.getDeclaredMethod("register", net.minecraft.world.inventory.MenuType.class, constructor);
            register.setAccessible(true);
            register.invoke(null, InteractionMenus.type.get(), factory);
            screenRegistered = true;
        } catch (ReflectiveOperationException exception) { throw new AssertionError(exception); }
    }
}
