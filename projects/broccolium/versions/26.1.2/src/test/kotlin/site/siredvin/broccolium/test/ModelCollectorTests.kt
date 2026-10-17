package site.siredvin.broccolium.test

import com.google.gson.JsonObject
import com.mojang.serialization.JsonOps
import net.minecraft.client.data.models.BlockModelGenerators
import net.minecraft.client.data.models.ItemModelGenerators
import net.minecraft.client.data.models.ItemModelOutput
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator
import net.minecraft.client.data.models.model.ModelInstance
import net.minecraft.client.renderer.block.dispatch.BlockStateModelDispatcher
import net.minecraft.client.renderer.item.ClientItem
import net.minecraft.client.renderer.item.ItemModel
import net.minecraft.core.Direction
import net.minecraft.resources.Identifier
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Blocks
import org.junit.jupiter.api.Test
import site.siredvin.broccolium.modules.data.model.createFlatItem
import site.siredvin.broccolium.modules.data.model.genericBlock
import site.siredvin.broccolium.modules.data.model.horizontalOrientatedBlock
import site.siredvin.broccolium.modules.data.model.horizontalOrientedModel
import site.siredvin.broccolium.modules.data.model.toYAngle
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlin.test.assertNotNull

@WithMinecraft
class ModelCollectorTests {
    @Test
    fun collectorsRetainDirectionsMaterialsAndItemDefinitions() {
        // Match native client/datagen bootstrap order before creating the
        // implementing record (its codec participates in this interface codec).
        assertNotNull(net.minecraft.client.renderer.block.dispatch.BlockStateModel.Unbaked.CODEC)
        val definitions = mutableListOf<BlockModelDefinitionGenerator>()
        val models = linkedMapOf<Identifier, ModelInstance>()
        val items = mutableListOf<Item>()
        val output = object : ItemModelOutput {
            override fun accept(item: Item, model: ItemModel.Unbaked, properties: ClientItem.Properties) { items.add(item) }
            override fun copy(source: Item, target: Item) { error("Unexpected model copy") }
        }
        val blocks = BlockModelGenerators(definitions::add, output) { id, model -> check(models.put(id, model) == null) }
        genericBlock(blocks, Blocks.STONE)
        val customFront = Identifier.fromNamespaceAndPath("broccolium", "block/custom_front")
        val furnaceModel = horizontalOrientedModel(blocks, Blocks.FURNACE, overwriteFront = customFront)
        horizontalOrientatedBlock(blocks, Blocks.FURNACE, furnaceModel)
        assertEquals(listOf(Items.STONE, Items.FURNACE), items)
        val furnace = models.getValue(furnaceModel).get().asJsonObject
        assertTrue(furnace.toString().contains(customFront.toString()), furnace.toString())
        assertEquals("minecraft:block/orientable", furnace.get("parent").asString)
        val dispatch = definitions.last().create()
        val json = BlockStateModelDispatcher.CODEC.encodeStart(JsonOps.INSTANCE, dispatch).getOrThrow().asJsonObject
        val variants = json.getAsJsonObject("variants")
        for ((direction, angle) in listOf(Direction.NORTH to 0, Direction.EAST to 90, Direction.SOUTH to 180, Direction.WEST to 270)) {
            val value = assertNotNull(variants.getAsJsonObject("facing=${direction.serializedName}"), json.toString())
            assertEquals(angle, value.get("y")?.asInt ?: 0, json.toString())
            assertEquals(furnaceModel.toString(), value.get("model").asString)
            assertEquals("R$angle", toYAngle(direction).toString())
        }
    }

    @Test
    fun flatLayersPreserveEveryTextureAndRejectInvalidCounts() {
        val models = linkedMapOf<Identifier, ModelInstance>()
        val output = object : ItemModelOutput {
            override fun accept(item: Item, model: ItemModel.Unbaked, properties: ClientItem.Properties) = Unit
            override fun copy(source: Item, target: Item) = Unit
        }
        val generator = ItemModelGenerators(output) { id, model -> models[id] = model }
        val id = Identifier.fromNamespaceAndPath("broccolium", "item/layers")
        val textures = Array(5) { Identifier.fromNamespaceAndPath("broccolium", "item/layer$it") }
        createFlatItem(generator, id, *textures)
        val json = models.getValue(id).get().asJsonObject
        assertEquals("minecraft:item/generated", json.get("parent").asString)
        for (index in textures.indices) assertTrue(json.getAsJsonObject("textures").get("layer$index").toString().contains(textures[index].toString()))
        assertFailsWith<IndexOutOfBoundsException> { createFlatItem(generator, id) }
        assertFailsWith<IndexOutOfBoundsException> { createFlatItem(generator, id, *Array(6) { textures[0] }) }
    }
}
