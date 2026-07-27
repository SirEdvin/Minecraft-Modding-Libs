// SPDX-FileCopyrightText: 2022 The CC: Tweaked Developers
// SPDX-FileCopyrightText: 2026 SirEdvin
// SPDX-License-Identifier: MPL-2.0
// Adapted from CC:Tweaked commit 6f16cd6b0e4b74afff5462d463bedba65764970e,
// projects/forge/src/testMod/java/dan200/computercraft/gametest/core/TestMod.java

package site.siredvin.testiarium.fixture.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.common.MinecraftForge;

public final class ForgeClientTestHooks {
    private ForgeClientTestHooks() {
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.addListener(ForgeClientTestHooks::onServerTick);
        MinecraftForge.EVENT_BUS.addListener(ForgeClientTestHooks::onOpenScreen);
        MinecraftForge.EVENT_BUS.addListener(ForgeClientTestHooks::onClientTick);
    }

    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        var screen = Minecraft.getInstance().screen;
        if (screen != null) ClientTestHooks.onOpenScreen(screen);
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) ClientTestHooks.onServerTick(event.getServer());
    }

    @SubscribeEvent
    public static void onOpenScreen(ScreenEvent.Opening event) {
        if (ClientTestHooks.onOpenScreen(event.getScreen())) event.setCanceled(true);
    }
}
