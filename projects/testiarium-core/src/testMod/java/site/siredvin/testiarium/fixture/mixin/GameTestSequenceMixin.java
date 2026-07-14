// SPDX-FileCopyrightText: 2022 The CC: Tweaked Developers
// SPDX-FileCopyrightText: 2026 SirEdvin
// SPDX-License-Identifier: MPL-2.0

package site.siredvin.testiarium.fixture.mixin;

import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestInfo;
import net.minecraft.gametest.framework.GameTestSequence;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GameTestSequence.class)
class GameTestSequenceMixin {
    @Shadow @Final GameTestInfo parent;

    @Overwrite
    public void tickAndContinue(long ticks) {
        try {
            tick(ticks);
        } catch (GameTestAssertException ignored) {
        } catch (AssertionError error) {
            parent.fail(error);
        } catch (Exception | LinkageError | VirtualMachineError error) {
            LoggerFactory.getLogger(GameTestSequenceMixin.class).error("{} threw unexpected exception", parent.getTestName(), error);
            parent.fail(error);
        }
    }

    @Shadow
    private void tick(long tick) {
    }
}
