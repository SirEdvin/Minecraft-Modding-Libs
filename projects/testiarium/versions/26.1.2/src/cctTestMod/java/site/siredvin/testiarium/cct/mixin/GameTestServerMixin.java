// SPDX-FileCopyrightText: 2025 The CC: Tweaked Developers
// SPDX-FileCopyrightText: 2026 SirEdvin
// SPDX-License-Identifier: MPL-2.0
package site.siredvin.testiarium.cct.mixin;

import java.util.concurrent.locks.LockSupport;
import net.minecraft.gametest.framework.GameTestServer;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import site.siredvin.testiarium.cct.CctComputers;

@Mixin(GameTestServer.class)
abstract class GameTestServerMixin {
    @Overwrite
    protected void waitUntilNextTick() {
        var server = (MinecraftServer) (Object) this;
        while (true) {
            ((BlockableEventLoopInvoker) server).testiarium$runAllTasks();
            if (!haveTestsStarted() || CctComputers.areComputersIdle(server)) break;
            LockSupport.parkNanos(100_000);
        }
    }

    @Shadow
    private boolean haveTestsStarted() {
        throw new AssertionError("Mixin shadow");
    }
}
