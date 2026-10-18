package site.siredvin.tweakium.mixins;

import dan200.computercraft.shared.turtle.core.TurtleBrain;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import site.siredvin.tweakium.modules.turtle.LegacyTurtleUpgradeData;

@Mixin(value = TurtleBrain.class, remap = false)
abstract class TurtleUpgradeDataMixin {
    @Inject(method = "readCommon", at = @At("HEAD"), remap = false)
    private void tweakium$restoreLegacyUpgradeData(
        CompoundTag tag, HolderLookup.Provider registries, CallbackInfo callback
    ) {
        LegacyTurtleUpgradeData.INSTANCE.restore(tag);
    }
}
