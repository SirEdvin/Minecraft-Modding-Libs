// SPDX-FileCopyrightText: 2022 The CC: Tweaked Developers
// SPDX-FileCopyrightText: 2026 SirEdvin
// SPDX-License-Identifier: MPL-2.0
// Adapted from CC:Tweaked commit 6f16cd6b0e4b74afff5462d463bedba65764970e,
// projects/forge/src/testMod/java/dan200/computercraft/gametest/core/TestMod.java

package site.siredvin.testiarium.fixture.client;

import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

public final class ForgeClientTestHooks {
    private ForgeClientTestHooks() {
    }

    public static void register() {
        NeoForge.EVENT_BUS.addListener(ForgeClientTestHooks::onServerTick);
        NeoForge.EVENT_BUS.addListener(ForgeClientTestHooks::onOpenScreen);
    }

    public static void onServerTick(ServerTickEvent.Pre event) {
        ClientTestHooks.onServerTick(event.getServer());
    }

    public static void onOpenScreen(ScreenEvent.Opening event) {
        if (ClientTestHooks.onOpenScreen(event.getScreen())) event.setCanceled(true);
    }
}
