package site.siredvin.tweakium.clienttest;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.TrackingItemStackRenderState;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import java.util.ArrayList;
import java.util.List;

/** Typed target-only consumer of the actual submission contract, not private model fields. */
final class RenderProof {
    private static final String DIAMOND = "minecraft:block/diamond_block";

    static void verify(Minecraft mc, List<ItemStack> gallery, String checkpoint) {
        for (int slot = 0; slot < gallery.size(); slot++) {
            ItemStack source = gallery.get(slot).copy();
            var state = new TrackingItemStackRenderState();
            mc.getItemModelResolver().updateForTopItem(state, source, ItemDisplayContext.GUI, mc.level, mc.player, 0);
            var before = submit(state);
            before.requireMaterials();
            // CC's pocket definition renders its computer frame, not the crafting item of its upgrade.
            if ((slot == 3 || slot == 4) && before.materials.stream().noneMatch(line -> line.startsWith(DIAMOND + "|"))) {
                throw new AssertionError("Equipped upgrade omitted diamond material at slot " + slot + ": " + before.materials);
            }
            if (slot == 5 && before.materials.stream().noneMatch(line -> line.startsWith("computercraft:item/pocket_computer_advanced|"))) {
                throw new AssertionError("Pocket computer omitted its advanced material");
            }
            // The submitting consumer only receives extracted state, not this mutable source stack.
            source.setCount(0);
            var after = submit(state);
            if (!before.materials.equals(after.materials) || !before.poses.equals(after.poses)) {
                throw new AssertionError("Mutable source leaked into render submission at slot " + slot);
            }
            ClientFixture.record("materials.log", "checkpoint=" + checkpoint + " slot=" + slot + " item="
                + BuiltInRegistries.ITEM.getKey(gallery.get(slot).getItem()) + " quads=" + before.materials.size()
                + " materials=" + new java.util.TreeSet<>(before.materials) + " snapshot=stable pose=restored");
        }
        // A nonempty missing-model render state used to pass ClientProof.verify. Keep this explicit
        // negative until an approved Creative Filler design exists; it is NOT all-content acceptance.
        var filler = BuiltInRegistries.ITEM.getValue(net.minecraft.resources.Identifier.fromNamespaceAndPath("tweakium", "creative_filler"));
        var missing = new TrackingItemStackRenderState();
        mc.getItemModelResolver().updateForTopItem(missing, new ItemStack(filler), ItemDisplayContext.GUI, mc.level, mc.player, 0);
        var probe = submit(missing);
        try {
            probe.requireMaterials();
        } catch (MissingMaterial expected) {
            ClientFixture.record("materials.log", "checkpoint=" + checkpoint + " negative=creative_filler missing-material-rejected all-content=BLOCKED");
            return;
        }
        throw new AssertionError("Creative Filler negative changed: replace probe with approved all-content assertion");
    }

    private static Materials submit(ItemStackRenderState state) {
        var poses = new PoseStack();
        var original = new Matrix4f(poses.last().pose());
        var result = new Materials();
        state.submit(poses, result, 0x00f000f0, 0, 0);
        if (!original.equals(poses.last().pose()) || !poses.isEmpty()) throw new AssertionError("Unbalanced render poses");
        return result;
    }
    private static final class MissingMaterial extends AssertionError {
        MissingMaterial(String message) { super(message); }
    }
    private static class Materials extends SubmitNodeStorage {
        final List<String> materials = new ArrayList<>();
        final List<Matrix4f> poses = new ArrayList<>();
        boolean missing;

        @Override public void submitItem(PoseStack stack, ItemDisplayContext context, int light, int overlay, int outline,
                int[] tints, List<BakedQuad> quads, ItemStackRenderState.FoilType foil) {
            for (var quad : quads) {
                var material = quad.materialInfo();
                var sprite = material.sprite();
                if (sprite.contents().name().equals(MissingTextureAtlasSprite.getLocation()) || material.itemRenderType() == null) missing = true;
                materials.add(sprite.contents().name() + "|" + material.layer() + "|atlas=" + sprite.atlasLocation()
                    + "|blend=" + (material.itemRenderType() != null && material.itemRenderType().hasBlending()));
                poses.add(new Matrix4f(stack.last().pose()));
            }
        }

        void requireMaterials() {
            if (materials.isEmpty() || missing) throw new MissingMaterial("Missing submitted material: " + materials);
        }
    }
}
