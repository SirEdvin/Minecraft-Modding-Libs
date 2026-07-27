// SPDX-FileCopyrightText: 2025 The CC: Tweaked Developers
// SPDX-FileCopyrightText: 2026 SirEdvin
// SPDX-License-Identifier: MPL-2.0
// Adapted from CC:Tweaked projects/common/src/testMod/java/dan200/computercraft/mixin/gametest/StructureUtilsMixin.java

package site.siredvin.testiarium.fixture.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.StructureUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.Set;

@Mixin(StructureUtils.class)
public class StructureUtilsMixin {
    @Inject(method = "getStructureTemplate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplateManager;readStructure(Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate;", shift = At.Shift.AFTER))
    private static void addMissingAir(String structureName, ServerLevel level, CallbackInfoReturnable<StructureTemplate> callback) {
        var template = callback.getReturnValue();
        var palette = ((StructureTemplateAccessor) template).getPalettes().get(0);
        Set<BlockPos> positions = new HashSet<>();
        for (var x = 0; x < template.getSize().getX(); x++) for (var y = 0; y < template.getSize().getY(); y++) for (var z = 0; z < template.getSize().getZ(); z++) positions.add(new BlockPos(x, y, z));
        for (var block : palette.blocks()) positions.remove(block.pos());
        for (var pos : positions) palette.blocks().add(new StructureTemplate.StructureBlockInfo(pos, Blocks.AIR.defaultBlockState(), null));
    }
}
