package site.siredvin.tweakium.testmod

import net.minecraft.gametest.framework.GameTestHelper
import site.siredvin.tweakium.modules.minecraft.blockentity.CreativeFillerBlockEntity

internal fun reloadCreativeFiller(original: CreativeFillerBlockEntity, restored: CreativeFillerBlockEntity, @Suppress("UNUSED_PARAMETER") helper: GameTestHelper) {
    restored.load(original.saveWithoutMetadata().copy())
}
