package site.siredvin.broccolium.modules.base.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import site.siredvin.broccolium.modules.base.api.IObservingBlockEntity;
import site.siredvin.broccolium.modules.base.block.BaseBlockEntityBlock;

@Mixin(LevelChunk.class)
public abstract class LevelChunkMixin {
    // Target the native block replacement path, not setRemoved(), which is also
    // used for chunk unloading. Keep native pre-remove drops and removal intact.
    @Redirect(
            method = "setBlockState",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/chunk/LevelChunk;removeBlockEntity(Lnet/minecraft/core/BlockPos;)V"),
            require = 1)
    private void broccolium$destroyRemovedObserver(LevelChunk chunk, BlockPos pos) {
        BlockEntity entity = chunk.getBlockEntity(pos);
        boolean observing = entity instanceof IObservingBlockEntity
                && entity.getBlockState().getBlock() instanceof BaseBlockEntityBlock<?>;
        chunk.removeBlockEntity(pos);
        if (observing) {
            ((IObservingBlockEntity) entity).destroy();
        }
    }
}
