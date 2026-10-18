package site.siredvin.tweakium.mixins;

import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import dan200.computercraft.shared.datafix.ComponentizationFixers;
import java.util.function.Function;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import site.siredvin.tweakium.modules.turtle.LegacyTurtleUpgradeFix;

@Mixin(value = ComponentizationFixers.class, remap = false)
abstract class TurtleUpgradeDataMixin {
    @Inject(method = "makeBlockEntityRewrites", at = @At("RETURN"), cancellable = true, remap = false)
    private static void tweakium$restoreLegacyUpgradeData(
        Schema input, Schema output, CallbackInfoReturnable<Function<Typed<?>, Typed<?>>> callback
    ) {
        callback.setReturnValue(LegacyTurtleUpgradeFix.before(input, callback.getReturnValue()));
    }
}
