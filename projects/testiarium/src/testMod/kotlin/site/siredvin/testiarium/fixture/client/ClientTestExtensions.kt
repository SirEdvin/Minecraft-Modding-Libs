// SPDX-FileCopyrightText: 2022 The CC: Tweaked Developers
// SPDX-FileCopyrightText: 2026 SirEdvin
// SPDX-License-Identifier: MPL-2.0
// Adapted from CC:Tweaked commit 6f16cd6b0e4b74afff5462d463bedba65764970e,
// projects/common/src/testMod/kotlin/dan200/computercraft/gametest/api/ClientTestExtensions.kt

package site.siredvin.testiarium.fixture.client

import net.minecraft.client.Minecraft
import net.minecraft.client.Screenshot
import net.minecraft.client.gui.screens.inventory.MenuAccess
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.gametest.framework.GameTestAssertException
import net.minecraft.gametest.framework.GameTestHelper
import net.minecraft.gametest.framework.GameTestSequence
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.EntityType
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import site.siredvin.testiarium.api.getEntity
import java.io.File
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException
import java.util.concurrent.atomic.AtomicBoolean

fun Minecraft.isRenderingStable(): Boolean = (this as MinecraftExtensions).`testiarium$isRenderingStable`()

fun GameTestSequence.thenOnClient(task: ClientTestHelper.() -> Unit): GameTestSequence {
    var future: CompletableFuture<Void>? = null
    thenExecute { future = Minecraft.getInstance().submit { task(ClientTestHelper()) } }
    thenWaitUntil { if (!future!!.isDone) throw GameTestAssertException("Client task has not completed") }
    thenExecute {
        try {
            future!!.get()
        } catch (error: ExecutionException) {
            throw error.cause ?: error
        }
    }
    return this
}

fun GameTestSequence.thenRenderIdle(ticks: Int = 20): GameTestSequence {
    var idleTicks = 0
    thenWaitUntil {
        if (Minecraft.getInstance().isRenderingStable()) {
            if (++idleTicks <= ticks) throw GameTestAssertException("Rendering has only been idle for $idleTicks ticks")
        } else {
            idleTicks = 0
            throw GameTestAssertException("Waiting for the client to finish rendering")
        }
    }
    return this
}

fun GameTestSequence.thenScreenshot(name: String? = null, showGui: Boolean = false): GameTestSequence {
    val screenshotName = "${name ?: "screenshot"}.png"
    val captured = AtomicBoolean()
    thenRenderIdle()
    thenOnClient { minecraft.options.hideGui = !showGui }
    thenIdle(2)
    thenOnClient { screenshot(screenshotName) { captured.set(true) } }
    thenWaitUntil { if (!captured.get()) throw GameTestAssertException("Screenshot was not captured") }
    thenOnClient { minecraft.options.hideGui = false }
    return this
}

fun ServerPlayer.setupForTest() {
    if (containerMenu != inventoryMenu) closeContainer()
}

fun GameTestHelper.positionAtArmorStand() {
    val stand = getEntity(EntityType.ARMOR_STAND)
    val player = level.randomPlayer ?: throw GameTestAssertException("Player does not exist")
    player.setupForTest()
    player.connection.teleport(stand.x, stand.y, stand.z, stand.yRot, stand.xRot)
}

fun GameTestHelper.positionAt(pos: BlockPos, yRot: Float = 0.0f, xRot: Float = 0.0f) {
    val absolutePos = absolutePos(pos)
    val player = level.randomPlayer ?: throw GameTestAssertException("Player does not exist")
    player.setupForTest()
    player.connection.teleport(absolutePos.x + 0.5, absolutePos.y + 0.5, absolutePos.z + 0.5, yRot, xRot)
}

class ClientTestHelper {
    val minecraft: Minecraft = Minecraft.getInstance()

    fun screenshot(name: String, callback: () -> Unit = {}) {
        val directory = File(System.getProperty("testiarium.screenshots", minecraft.gameDirectory.absolutePath))
        Screenshot.grab(directory, name, minecraft.mainRenderTarget) { callback() }
    }

    fun <T : AbstractContainerMenu> getOpenMenu(type: MenuType<T>): T {
        val screen = minecraft.screen
        val name = BuiltInRegistries.MENU.getKey(type)
        @Suppress("UNCHECKED_CAST")
        return when {
            screen == null -> throw GameTestAssertException("Expected a $name menu, but no screen is open")
            screen !is MenuAccess<*> -> throw GameTestAssertException("Expected a $name menu, but $screen is open")
            screen.menu.type != type -> throw GameTestAssertException("Expected a $name menu, but ${BuiltInRegistries.MENU.getKey(screen.menu.type)} is open")
            else -> screen.menu as T
        }
    }
}
