// SPDX-FileCopyrightText: 2022 The CC: Tweaked Developers
// SPDX-FileCopyrightText: 2026 SirEdvin
// SPDX-License-Identifier: MPL-2.0
// Adapted from CC:Tweaked commit 6f16cd6b0e4b74afff5462d463bedba65764970e,
// projects/common/src/testMod/java/dan200/computercraft/mixin/gametest/client/WorldOpenFlowsMixin.java

package site.siredvin.testiarium.fixture.mixin.client;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.WorldOpenFlows;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(WorldOpenFlows.class)
class WorldOpenFlowsMixin {
    /** Never show a backup prompt during an unattended test run. */
    @Overwrite
    private void askForBackup(Screen screen, String level, boolean customised, Runnable action) {
        action.run();
    }
}
