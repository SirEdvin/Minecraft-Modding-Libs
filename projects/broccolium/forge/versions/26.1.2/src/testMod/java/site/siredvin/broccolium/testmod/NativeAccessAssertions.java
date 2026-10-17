package site.siredvin.broccolium.testmod;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import site.siredvin.broccolium.modules.storage.item.AgnosticItemAccess;
import site.siredvin.broccolium.modules.storage.item.ContainerWrapper;
import site.siredvin.broccolium.modules.storage.item.CompletePlayerInventory;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.PlayerInventoryWrapper;

/** Test-only Java access to the Kotlin-internal bridge, never a public API. */
public final class NativeAccessAssertions {
    private NativeAccessAssertions() {}

    public static void completePlayerRollback(Player player) {
        int last = player.getInventory().getContainerSize() - 1;
        ItemStack original = new ItemStack(Items.EMERALD, 7);
        original.set(DataComponents.CUSTOM_NAME, Component.literal("equipment rollback"));
        original.remove(DataComponents.ENCHANTMENTS);
        player.getInventory().setItem(last, original.copy());
        var main = PlayerInventoryWrapper.of(player);
        var handler = CompletePlayerInventory.INSTANCE.of(player, main);
        if (handler.size() != player.getInventory().getContainerSize()) throw new AssertionError("Incomplete native player inventory");
        if (handler.isValid(Inventory.INVENTORY_SIZE, ItemResource.of(Items.DIAMOND))) throw new AssertionError("Native armor insertion rule bypassed");
        try (var outer = Transaction.openRoot()) {
            try (var nested = Transaction.open(outer)) {
                if (handler.extract(last, ItemResource.of(original), 7, nested) != 7) throw new AssertionError("Extra equipment extraction failed");
                nested.commit();
            }
            if (!player.getInventory().getItem(last).isEmpty()) throw new AssertionError("Extra equipment mutation missing");
            if (CompletePlayerInventory.INSTANCE.of(player, main) != handler) throw new AssertionError("Repeated lookup did not share native journals");
        }
        if (!ItemStack.matches(original, player.getInventory().getItem(last))) throw new AssertionError("Extra equipment rollback lost components/count");
    }

    public static void nestedBucketRollback(ContainerWrapper origin) {
        ItemStack original = new ItemStack(Items.BUCKET);
        original.set(DataComponents.CUSTOM_NAME, Component.literal("rollback marker"));
        original.remove(DataComponents.ENCHANTMENTS);
        origin.replaceSlot(0, original);
        var access = AgnosticItemAccess.Companion.of(origin, 0);
        var handler = java.util.Objects.requireNonNull(access.getCapability(Capabilities.Fluid.ITEM));
        try (var outer = Transaction.openRoot()) {
            try (var nested = Transaction.open(outer)) {
                if (handler.insert(FluidResource.of(Fluids.WATER), 1000, nested) != 1000) throw new AssertionError("Native bucket refused water");
                nested.commit();
            }
            if (!origin.get(0).is(Items.WATER_BUCKET)) throw new AssertionError("Native ItemAccess did not replace origin");
        }
        if (!ItemStack.matches(original, origin.get(0))) throw new AssertionError("Nested rollback lost exact origin components/count");
        origin.replaceSlot(0, new ItemStack(Items.BUCKET));
    }
}
