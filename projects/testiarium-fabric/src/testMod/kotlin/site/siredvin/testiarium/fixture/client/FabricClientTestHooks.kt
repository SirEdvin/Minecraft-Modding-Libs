// SPDX-FileCopyrightText: 2022 The CC: Tweaked Developers
// SPDX-FileCopyrightText: 2026 SirEdvin
// SPDX-License-Identifier: MPL-2.0
// Adapted from CC:Tweaked commit 6f16cd6b0e4b74afff5462d463bedba65764970e,
// projects/fabric/src/testMod/java/dan200/computercraft/gametest/core/TestMod.java

package site.siredvin.testiarium.fixture.client

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents

object FabricClientTestHooks : ClientModInitializer {
    override fun onInitializeClient() {
        ServerTickEvents.START_SERVER_TICK.register(ClientTestHooks::onServerTick)
        ScreenEvents.AFTER_INIT.register { _, screen, _, _ -> ClientTestHooks.onOpenScreen(screen) }
    }
}
