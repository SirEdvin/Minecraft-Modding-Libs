package site.siredvin.tweakium.test

import net.minecraft.world.item.Items
import site.siredvin.broccolium.test.WithMinecraft
import site.siredvin.tweakium.modules.plugins.PeripheralPluginUtils
import kotlin.test.Test
import kotlin.test.assertFalse

@WithMinecraft
@WithTweakium
class PredicateTests {
    @Test
    fun testBasicNameItemPredicate() {
        val predicate = PeripheralPluginUtils.itemQueryToPredicate("minecraft:cobblestone")
        assert(predicate.test(Items.COBBLESTONE.defaultInstance))
        assertFalse(predicate.test(Items.GRASS_BLOCK.defaultInstance))
        val expandedPredicate = PeripheralPluginUtils.itemQueryToPredicate(
            mapOf(
                "name" to "minecraft:cobblestone",
            ),
        )
        assert(expandedPredicate.test(Items.COBBLESTONE.defaultInstance))
        assertFalse(expandedPredicate.test(Items.GRASS_BLOCK.defaultInstance))
    }

    @Test
    fun testOrPredicate() {
        val expandedPredicate = PeripheralPluginUtils.itemQueryToPredicate(
            mapOf(
                "or" to mapOf<Any, Any>(
                    1 to mapOf(
                        "name" to "minecraft:cobblestone",
                    ),
                    2 to mapOf(
                        "name" to "minecraft:stone",
                    ),
                ),
            ),
        )
        assert(expandedPredicate.test(Items.COBBLESTONE.defaultInstance))
        assert(expandedPredicate.test(Items.STONE.defaultInstance))
        assertFalse(expandedPredicate.test(Items.GRASS_BLOCK.defaultInstance))
    }

    @Test
    fun testInPredicate() {
        val expandedPredicate = PeripheralPluginUtils.itemQueryToPredicate(
            mapOf(
                "name" to mapOf<Any, Any>(
                    "in" to mapOf(
                        1 to "minecraft:cobblestone",
                        2 to "minecraft:stone",
                    ),
                ),
            ),
        )
        assert(expandedPredicate.test(Items.COBBLESTONE.defaultInstance))
        assert(expandedPredicate.test(Items.STONE.defaultInstance))
        assertFalse(expandedPredicate.test(Items.GRASS_BLOCK.defaultInstance))
    }

    @Test
    fun testNotInPredicate() {
        val expandedPredicate = PeripheralPluginUtils.itemQueryToPredicate(
            mapOf(
                "name" to mapOf<Any, Any>(
                    "not_in" to mapOf(
                        1 to "minecraft:cobblestone",
                        2 to "minecraft:stone",
                    ),
                ),
            ),
        )
        assertFalse(expandedPredicate.test(Items.COBBLESTONE.defaultInstance))
        assertFalse(expandedPredicate.test(Items.STONE.defaultInstance))
        assert(expandedPredicate.test(Items.GRASS_BLOCK.defaultInstance))
    }
}
