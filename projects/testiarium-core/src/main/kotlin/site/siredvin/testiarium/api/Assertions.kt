// SPDX-FileCopyrightText: 2022 The CC: Tweaked Developers
// SPDX-FileCopyrightText: 2026 SirEdvin
// SPDX-License-Identifier: MPL-2.0
// Adapted from CC:Tweaked projects/common/src/testMod/kotlin/dan200/computercraft/gametest/api/TestExtensions.kt

package site.siredvin.testiarium.api

import net.minecraft.core.BlockPos
import net.minecraft.gametest.framework.GameTestAssertException
import net.minecraft.gametest.framework.GameTestHelper
import net.minecraft.gametest.framework.GameTestSequence
import net.minecraft.world.Container
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.inventory.TransientCraftingContainer
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.Property

/** Run a sequence action which converts all failures into GameTest failures. */
fun GameTestSequence.thenExecuteFailFast(action: () -> Unit): GameTestSequence = thenExecute {
    try {
        action()
    } catch (error: GameTestAssertException) {
        throw error
    } catch (error: Throwable) {
        throw GameTestAssertException(error.message ?: error.javaClass.name)
    }
}

fun GameTestHelper.assertBlock(pos: BlockPos, predicate: (BlockState) -> Boolean, message: String = "") {
    val state = getBlockState(pos)
    if (!predicate(state)) failAt(pos, message.ifEmpty { "Unexpected block state $state" })
}

fun <T : Comparable<T>> GameTestHelper.assertBlockProperty(
    pos: BlockPos,
    property: Property<T>,
    expected: T,
    message: String = "",
) {
    val state = getBlockState(pos)
    when {
        !state.hasProperty(property) -> failAt(pos, message.ifEmpty { "${state.block} has no ${property.name} property" })
        state.getValue(property) != expected -> failAt(pos, message.ifEmpty { "${property.name} is ${state.getValue(property)}, expected $expected" })
    }
}

fun GameTestHelper.getContainer(pos: BlockPos): Container = when (val blockEntity = getBlockEntity(pos)) {
    is Container -> blockEntity
    null -> failAt(pos, "Expected a container, found nothing")
    else -> failAt(pos, "Expected a container, found ${blockEntity.type}")
}

fun GameTestHelper.assertContainer(pos: BlockPos, expected: List<ItemStack>) {
    val container = getContainer(pos)
    val actual = List(container.containerSize, container::getItem)
    val mismatch = actual.indices.firstOrNull { index ->
        !ItemStack.matches(actual[index], expected.getOrElse(index) { ItemStack.EMPTY })
    }
    if (mismatch != null) failAt(pos, "Container differs at slot $mismatch. Expected $expected, got $actual")
}

fun <T : BlockEntity> GameTestHelper.getBlockEntity(pos: BlockPos, type: BlockEntityType<T>): T {
    val blockEntity = getBlockEntity(pos)
    if (blockEntity == null || blockEntity.type != type) {
        failAt(pos, "Expected $type, got ${blockEntity?.type}")
    }
    @Suppress("UNCHECKED_CAST")
    return blockEntity as T
}

fun <T : Entity> GameTestHelper.getEntity(type: EntityType<T>): T {
    val entities = getEntities(type, BlockPos.ZERO, 64.0)
    if (entities.size != 1) throw GameTestAssertException("Expected one $type, found ${entities.size}")
    return entities.single()
}

fun GameTestHelper.assertItemCount(item: Item, expected: Int) {
    val actual = getEntities(EntityType.ITEM, BlockPos.ZERO, 64.0).sumOf { stack -> if (stack.item.`is`(item)) stack.item.count else 0 }
    if (actual != expected) throw GameTestAssertException("Expected $expected ${item.description.string}, found $actual")
}

fun GameTestHelper.assertCraftable(items: List<ItemStack>, expected: ItemStack) {
    val container = TransientCraftingContainer(DummyMenu, 3, 3).also { crafting ->
        items.forEachIndexed { index, item -> crafting.setItem(index, item) }
    }
    val recipe = level.server.recipeManager.getRecipeFor(RecipeType.CRAFTING, container, level)
        .orElseThrow { GameTestAssertException("No recipe matches $items") }
    val actual = recipe.assemble(container, level.registryAccess())
    if (!ItemStack.matches(actual, expected)) {
        throw GameTestAssertException("Expected $items to craft $expected, got $actual")
    }
}

private fun GameTestHelper.failAt(pos: BlockPos, message: String): Nothing = throw GameTestAssertException("$message at $pos")

private object DummyMenu : AbstractContainerMenu(MenuType.GENERIC_9x1, 0) {
    override fun quickMoveStack(player: net.minecraft.world.entity.player.Player, slot: Int) = ItemStack.EMPTY

    override fun stillValid(player: net.minecraft.world.entity.player.Player) = true
}
