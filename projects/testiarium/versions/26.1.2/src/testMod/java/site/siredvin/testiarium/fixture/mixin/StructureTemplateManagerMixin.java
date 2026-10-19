// SPDX-FileCopyrightText: 2023 The CC: Tweaked Developers
// SPDX-FileCopyrightText: 2026 SirEdvin
// SPDX-License-Identifier: MPL-2.0

package site.siredvin.testiarium.fixture.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Mixin(StructureTemplateManager.class)
class StructureTemplateManagerMixin {
    @Inject(method = "get", at = @At("RETURN"))
    private void loadFromTestStructures(Identifier id, CallbackInfoReturnable<Optional<StructureTemplate>> callback) {
        callback.getReturnValue().ifPresent(StructureTemplateManagerMixin::addMissingAir);
    }

    private static void addMissingAir(StructureTemplate template) {
        var size = template.getSize();
        if (((StructureTemplateAccessor) template).getPalettes().isEmpty()) return;
        var palette = ((StructureTemplateAccessor) template).getPalettes().get(0);
        Set<BlockPos> positions = new HashSet<>();
        for (var x = 0; x < size.getX(); x++) {
            for (var y = 0; y < size.getY(); y++) {
                for (var z = 0; z < size.getZ(); z++) positions.add(new BlockPos(x, y, z));
            }
        }
        for (var block : palette.blocks()) positions.remove(block.pos());
        for (var pos : positions) {
            palette.blocks().add(new StructureTemplate.StructureBlockInfo(pos, Blocks.AIR.defaultBlockState(), null));
        }
    }
}
