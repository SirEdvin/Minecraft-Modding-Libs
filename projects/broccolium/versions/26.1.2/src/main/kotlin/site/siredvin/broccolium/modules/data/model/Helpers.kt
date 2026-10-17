package site.siredvin.broccolium.modules.data.model

import com.mojang.math.Quadrant
import net.minecraft.client.data.models.BlockModelGenerators
import net.minecraft.client.data.models.ItemModelGenerators
import net.minecraft.client.data.models.MultiVariant
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator
import net.minecraft.client.data.models.blockstates.PropertyDispatch
import net.minecraft.client.data.models.model.*
import net.minecraft.client.renderer.block.dispatch.Variant
import net.minecraft.client.renderer.block.dispatch.VariantMutator
import net.minecraft.client.resources.model.sprite.Material
import net.minecraft.core.Direction
import net.minecraft.resources.Identifier
import net.minecraft.util.random.WeightedList
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import java.util.*

fun createFlatItem(generators: ItemModelGenerators, item: Item, vararg textures: Identifier) {
    val model = ModelLocationUtils.getModelLocation(item)
    createFlatItem(generators, model, *textures)
    generators.itemModelOutput.accept(item, ItemModelUtils.plainModel(model))
}

fun createFlatItem(
    generators: ItemModelGenerators,
    model: Identifier,
    vararg textures: Identifier,
) {
    if (textures.size > 5) throw IndexOutOfBoundsException("Too many layers")
    if (textures.isEmpty()) throw IndexOutOfBoundsException("Must have at least one texture")
    if (textures.size == 1) {
        ModelTemplates.FLAT_ITEM.create(model, TextureMapping.layer0(Material(textures[0])), generators.modelOutput)
        return
    }
    val slots = Array(textures.size) { i -> TextureSlot.create("layer$i") }
    val mapping = TextureMapping()
    slots.forEachIndexed { index, textureSlot ->
        mapping.put(textureSlot, Material(textures[index]))
    }
    ModelTemplate(Optional.of(Identifier.withDefaultNamespace("item/generated")), Optional.empty(), *slots)
        .create(model, mapping, generators.modelOutput)
}

fun toYAngle(direction: Direction): Quadrant = when (direction) {
    Direction.NORTH -> Quadrant.R0
    Direction.SOUTH -> Quadrant.R180
    Direction.EAST -> Quadrant.R90
    Direction.WEST -> Quadrant.R270
    else -> Quadrant.R270
}

fun createHorizontalFacingDispatch(): PropertyDispatch<VariantMutator> {
    val dispatch = PropertyDispatch.modify(BlockStateProperties.HORIZONTAL_FACING)
    for (direction in BlockStateProperties.HORIZONTAL_FACING.possibleValues) {
        dispatch.select(direction, VariantMutator.Y_ROT.withValue(toYAngle(direction)))
    }
    return dispatch
}

fun genericBlock(generators: BlockModelGenerators, block: Block) {
    val model = ModelTemplates.CUBE_ALL.create(
        block,
        TextureMapping.cube(block).put(TextureSlot.ALL, TextureMapping.getBlockTexture(block)),
        generators.modelOutput,
    )
    generators.blockStateOutput.accept(MultiVariantGenerator.dispatch(block, MultiVariant(WeightedList.of(Variant(model)))))
    generators.itemModelOutput.accept(block.asItem(), ItemModelUtils.plainModel(model))
}

fun horizontalOrientedModel(
    generators: BlockModelGenerators,
    block: Block,
    overwriteSide: Identifier? = null,
    overwriteTop: Identifier? = null,
    overwriteBottom: Identifier? = null,
    overwriteFront: Identifier? = null,
): Identifier {
    val textureMapping = TextureMapping.orientableCube(block)
    if (overwriteSide != null) textureMapping.put(TextureSlot.SIDE, Material(overwriteSide))
    if (overwriteBottom != null) textureMapping.put(TextureSlot.BOTTOM, Material(overwriteBottom))
    if (overwriteTop != null) textureMapping.put(TextureSlot.TOP, Material(overwriteTop))
    if (overwriteFront != null) textureMapping.put(TextureSlot.FRONT, Material(overwriteFront))
    return ModelTemplates.CUBE_ORIENTABLE.create(block, textureMapping, generators.modelOutput)
}

fun horizontalOrientatedBlock(
    generators: BlockModelGenerators,
    block: Block,
    model: Identifier? = null,
) {
    val modelLocation = model ?: ModelLocationUtils.getModelLocation(block)
    generators.blockStateOutput.accept(
        MultiVariantGenerator.dispatch(block, MultiVariant(WeightedList.of(Variant(modelLocation))))
            .with(createHorizontalFacingDispatch()),
    )
    generators.itemModelOutput.accept(block.asItem(), ItemModelUtils.plainModel(modelLocation))
}
