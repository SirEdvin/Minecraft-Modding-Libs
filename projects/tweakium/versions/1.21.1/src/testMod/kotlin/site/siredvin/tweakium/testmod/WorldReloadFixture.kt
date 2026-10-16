package site.siredvin.tweakium.testmod

import net.minecraft.gametest.framework.GameTestHelper
import site.siredvin.tweakium.modules.minecraft.blockentity.CreativeFillerBlockEntity

internal fun reloadCreativeFiller(original: CreativeFillerBlockEntity, restored: CreativeFillerBlockEntity, helper: GameTestHelper) {
    restored.loadWithComponents(original.saveWithoutMetadata(helper.level.registryAccess()).copy(), helper.level.registryAccess())
}
