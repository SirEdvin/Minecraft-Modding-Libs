// SPDX-FileCopyrightText: 2022 The CC: Tweaked Developers
// SPDX-FileCopyrightText: 2026 SirEdvin
// SPDX-License-Identifier: MPL-2.0
// Adapted from CC:Tweaked projects/common/src/testMod/kotlin/dan200/computercraft/gametest/api/TestExtensions.kt

package site.siredvin.testiarium.api

import net.minecraft.core.BlockPos
import net.minecraft.core.NonNullList
import net.minecraft.gametest.framework.GameTestAssertException
import net.minecraft.gametest.framework.GameTestHelper
import net.minecraft.gametest.framework.GameTestSequence
import net.minecraft.network.chat.Component
import net.minecraft.world.Container
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.CraftingInput
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.Property

private val sequenceParent = GameTestSequence::class.java.getDeclaredField("parent").also { it.isAccessible = true }
private val finishTest = net.minecraft.gametest.framework.GameTestInfo::class.java.getDeclaredMethod("finish").also { it.isAccessible = true }

/** Native fail() only records the error in 26.1; finish and stop this sequence in the same tick. */
fun GameTestSequence.thenExecuteFailFast(action: () -> Unit): GameTestSequence = thenWaitUntil {
    val parent = sequenceParent.get(this) as net.minecraft.gametest.framework.GameTestInfo
    if (parent.error != null) throw GameTestAssertException(Component.literal("Sequence already failed"), 0)
    try {
        action()
    } catch (error: Throwable) {
        parent.fail(error as? net.minecraft.gametest.framework.GameTestException ?: net.minecraft.gametest.framework.UnknownGameTestException(error))
        finishTest.invoke(parent)
        // A native wait assertion stops the event iterator without replacing the original error.
        throw GameTestAssertException(Component.literal("Sequence stopped after failure"), 0)
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

fun GameTestHelper.getContainer(pos: BlockPos): Container = when (val blockEntity = getBlockEntity(pos, BlockEntity::class.java)) {
    is Container -> blockEntity
    else -> failAt(pos, "Expected a container, found ${blockEntity.type}")
}

fun GameTestHelper.assertContainer(pos: BlockPos, expected: List<ItemStack>) {
    val container = getContainer(pos)
    val actual = List(container.containerSize, container::getItem)
    val mismatch = (0 until maxOf(actual.size, expected.size)).firstOrNull { index ->
        !ItemStack.matches(actual.getOrElse(index) { ItemStack.EMPTY }, expected.getOrElse(index) { ItemStack.EMPTY })
    }
    if (mismatch != null) failAt(pos, "Container differs at slot $mismatch. Expected $expected, got $actual")
}

fun <T : BlockEntity> GameTestHelper.getBlockEntity(pos: BlockPos, type: BlockEntityType<T>): T {
    val blockEntity = getBlockEntity(pos, BlockEntity::class.java)
    if (blockEntity.type != type) {
        failAt(pos, "Expected $type, got ${blockEntity.type}")
    }
    @Suppress("UNCHECKED_CAST")
    return blockEntity as T
}

fun <T : Entity> GameTestHelper.getEntity(type: EntityType<T>): T {
    val entities = getEntities(type, BlockPos.ZERO, 64.0)
    if (entities.size != 1) throw assertionException(Component.literal("Expected one $type, found ${entities.size}"))
    return entities.single()
}

fun GameTestHelper.assertItemCount(item: Item, expected: Int) {
    val actual = getEntities(EntityType.ITEM, BlockPos.ZERO, 64.0).sumOf { stack -> if (stack.item.`is`(item)) stack.item.count else 0 }
    if (actual != expected) throw assertionException(Component.literal("Expected $expected $item, found $actual"))
}

fun GameTestHelper.assertCraftable(items: List<ItemStack>, expected: ItemStack) {
    val stacks = NonNullList.withSize(9, ItemStack.EMPTY)
    items.forEachIndexed { index, item -> stacks[index] = item }
    val input = CraftingInput.of(3, 3, stacks)
    val recipe = level.server.recipeManager.getRecipeFor(RecipeType.CRAFTING, input, level)
        .orElseThrow { assertionException(Component.literal("No recipe matches $items")) }
    val actual = recipe.value.assemble(input)
    if (!ItemStack.matches(actual, expected)) {
        throw assertionException(Component.literal("Expected $items to craft $expected, got $actual"))
    }
}

private fun GameTestHelper.failAt(pos: BlockPos, message: String): Nothing = throw assertionException(Component.literal("$message at $pos"))
