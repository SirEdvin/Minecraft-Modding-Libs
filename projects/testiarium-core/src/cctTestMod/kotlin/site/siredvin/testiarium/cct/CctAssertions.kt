// SPDX-FileCopyrightText: 2022 The CC: Tweaked Developers
// SPDX-FileCopyrightText: 2026 SirEdvin
// SPDX-License-Identifier: MPL-2.0
// Adapted from CC:Tweaked 1.113.0 projects/common/src/testMod/kotlin/dan200/computercraft/gametest/api/TestExtensions.kt.

package site.siredvin.testiarium.cct

import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.gametest.framework.GameTestAssertException
import net.minecraft.gametest.framework.GameTestHelper
import net.minecraft.world.item.ItemStack
import site.siredvin.testiarium.api.assertContainer

fun GameTestHelper.assertCctBlock(pos: BlockPos, path: String) {
    val actual = BuiltInRegistries.BLOCK.getKey(getBlockState(pos).block)
    if (actual.namespace != "computercraft" || actual.path != path) {
        throw GameTestAssertException("Expected computercraft:$path at $pos, found $actual")
    }
}

fun GameTestHelper.assertComputer(pos: BlockPos) = assertCctBlock(pos, "computer_normal")
fun GameTestHelper.assertDiskDrive(pos: BlockPos) = assertCctBlock(pos, "disk_drive")
fun GameTestHelper.assertModem(pos: BlockPos) = assertCctBlock(pos, "wireless_modem_normal")
fun GameTestHelper.assertMonitor(pos: BlockPos) = assertCctBlock(pos, "monitor_normal")
fun GameTestHelper.assertPrinter(pos: BlockPos) = assertCctBlock(pos, "printer")
fun GameTestHelper.assertSpeaker(pos: BlockPos) = assertCctBlock(pos, "speaker")
fun GameTestHelper.assertTurtle(pos: BlockPos) = assertCctBlock(pos, "turtle_normal")
fun GameTestHelper.assertRelay(pos: BlockPos) = assertCctBlock(pos, "wired_modem_full")
fun GameTestHelper.assertInventory(pos: BlockPos, expected: List<ItemStack>) = assertContainer(pos, expected)
fun GameTestHelper.assertCraftOsFile(label: String) = require(label.isNotBlank()) { "Computer label must select a Lua test file" }
fun GameTestHelper.assertPocketComputer(stack: ItemStack) = assertCctItem(stack, "pocket_computer_normal")
fun GameTestHelper.assertPrintout(stack: ItemStack) = assertCctItem(stack, "printed_page")
fun GameTestHelper.assertDisk(stack: ItemStack) = assertCctItem(stack, "disk")
fun GameTestHelper.assertLoot(stack: ItemStack, expectedPath: String) = assertCctItem(stack, expectedPath)
fun GameTestHelper.assertRecipeResult(stack: ItemStack, expectedPath: String) = assertCctItem(stack, expectedPath)

private fun GameTestHelper.assertCctItem(stack: ItemStack, path: String) {
    val actual = BuiltInRegistries.ITEM.getKey(stack.item)
    if (actual.namespace != "computercraft" || actual.path != path) {
        throw GameTestAssertException("Expected computercraft:$path, found $actual")
    }
}
