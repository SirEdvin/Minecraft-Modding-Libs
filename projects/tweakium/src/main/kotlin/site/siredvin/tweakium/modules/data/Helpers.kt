package site.siredvin.tweakium.modules.data

import net.minecraft.data.models.ItemModelGenerators
import net.minecraft.data.models.model.ModelTemplate
import net.minecraft.data.models.model.TextureMapping
import net.minecraft.data.models.model.TextureSlot
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import site.siredvin.broccolium.modules.platform.PlatformRegistries
import site.siredvin.tweakium.TweakiumCore
import java.util.*

val TURTLE_LEFT_UPGRADE = ModelTemplate(
    Optional.of(ResourceLocation(TweakiumCore.MOD_ID, "base/upgrade_base_left")),
    Optional.empty(),
    TextureSlot.TEXTURE,
)

val TURTLE_RIGHT_UPGRADE = ModelTemplate(
    Optional.of(ResourceLocation(TweakiumCore.MOD_ID, "base/upgrade_base_right")),
    Optional.empty(),
    TextureSlot.TEXTURE,
)

fun turtleUpgrades(generators: ItemModelGenerators, block: Block, textureSuffix: String = "", baseID: ResourceLocation? = null) {
    val realBaseID = baseID ?: PlatformRegistries.BLOCKS.getKey(block).withPrefix("turtle/")
    TURTLE_RIGHT_UPGRADE.create(
        realBaseID.withSuffix("_right"),
        TextureMapping().put(TextureSlot.TEXTURE, TextureMapping.getBlockTexture(block).withSuffix(textureSuffix)),
        generators.output,
    )
    TURTLE_LEFT_UPGRADE.create(
        realBaseID.withSuffix("_left"),
        TextureMapping().put(TextureSlot.TEXTURE, TextureMapping.getBlockTexture(block).withSuffix(textureSuffix)),
        generators.output,
    )
}

fun turtleUpgrades(generators: ItemModelGenerators, item: Item, textureSuffix: String = "", baseID: ResourceLocation? = null) {
    val realBaseID = baseID ?: PlatformRegistries.ITEMS.getKey(item).withPrefix("turtle/")
    TURTLE_RIGHT_UPGRADE.create(
        realBaseID.withSuffix("_right"),
        TextureMapping().put(TextureSlot.TEXTURE, TextureMapping.getItemTexture(item).withSuffix(textureSuffix)),
        generators.output,
    )
    TURTLE_LEFT_UPGRADE.create(
        realBaseID.withSuffix("_left"),
        TextureMapping().put(TextureSlot.TEXTURE, TextureMapping.getItemTexture(item).withSuffix(textureSuffix)),
        generators.output,
    )
}
