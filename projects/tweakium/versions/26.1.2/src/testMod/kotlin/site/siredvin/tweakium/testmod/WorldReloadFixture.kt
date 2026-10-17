package site.siredvin.tweakium.testmod

import net.minecraft.gametest.framework.GameTestHelper
import net.minecraft.world.level.storage.TagValueInput
import net.minecraft.util.ProblemReporter
import site.siredvin.tweakium.modules.minecraft.blockentity.CreativeFillerBlockEntity

internal fun reloadCreativeFiller(original: CreativeFillerBlockEntity, restored: CreativeFillerBlockEntity, helper: GameTestHelper) {
    restored.loadWithComponents(TagValueInput.create(ProblemReporter.DISCARDING, helper.level.registryAccess(), original.saveWithoutMetadata(helper.level.registryAccess()).copy()))
}
