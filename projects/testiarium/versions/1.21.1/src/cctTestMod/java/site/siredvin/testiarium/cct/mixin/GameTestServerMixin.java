// SPDX-FileCopyrightText: 2025 The CC: Tweaked Developers
// SPDX-FileCopyrightText: 2026 SirEdvin
// SPDX-License-Identifier: MPL-2.0

package site.siredvin.testiarium.cct.mixin;

import com.mojang.datafixers.DataFixer;
import net.minecraft.gametest.framework.GameTestServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Services;
import net.minecraft.server.WorldStem;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import site.siredvin.testiarium.cct.CctComputers;

import java.net.Proxy;
import java.util.concurrent.locks.LockSupport;

@Mixin(GameTestServer.class)
abstract class GameTestServerMixin extends MinecraftServer {
    GameTestServerMixin(Thread serverThread, LevelStorageSource.LevelStorageAccess storageSource, PackRepository packRepository, WorldStem worldStem, Proxy proxy, DataFixer fixerUpper, Services services, ChunkProgressListenerFactory progressListenerFactory) {
        super(serverThread, storageSource, packRepository, worldStem, proxy, fixerUpper, services, progressListenerFactory);
    }

    @Overwrite
    @Override
    public void waitUntilNextTick() {
        while (true) {
            runAllTasks();
            if (!haveTestsStarted() || CctComputers.areComputersIdle(this)) break;
            LockSupport.parkNanos(100_000);
        }
    }

    @Shadow
    private boolean haveTestsStarted() {
        throw new AssertionError("Stub.");
    }
}
