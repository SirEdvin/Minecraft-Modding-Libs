// SPDX-FileCopyrightText: 2022 The CC: Tweaked Developers
// SPDX-FileCopyrightText: 2026 SirEdvin
// SPDX-License-Identifier: MPL-2.0
// Adapted from CC:Tweaked commit 6f16cd6b0e4b74afff5462d463bedba65764970e,
// projects/common/src/testMod/java/dan200/computercraft/mixin/gametest/client/MinecraftMixin.java

package site.siredvin.testiarium.fixture.mixin.client;

import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import site.siredvin.testiarium.fixture.client.MinecraftExtensions;

@Mixin(Minecraft.class)
class MinecraftMixin implements MinecraftExtensions {
    @Final @Shadow public LevelRenderer levelRenderer;
    @Shadow public ClientLevel level;
    @Shadow public LocalPlayer player;
    @Unique private final AtomicBoolean testiarium$isStable = new AtomicBoolean(false);

    @Inject(method = "runTick", at = @At("TAIL"))
    private void testiarium$updateStable(boolean render, CallbackInfo callback) {
        testiarium$isStable.set(
            level != null && player != null
                && levelRenderer.isSectionCompiled(player.blockPosition())
                && levelRenderer.countRenderedSections() > 10
                && levelRenderer.hasRenderedAllSections()
        );
    }

    @Override
    public boolean testiarium$isRenderingStable() {
        return testiarium$isStable.get();
    }
}
