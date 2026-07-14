// SPDX-FileCopyrightText: 2022 The CC: Tweaked Developers
// SPDX-FileCopyrightText: 2026 SirEdvin
// SPDX-License-Identifier: MPL-2.0
// Adapted from CC:Tweaked commit 6f16cd6b0e4b74afff5462d463bedba65764970e,
// projects/common/src/testMod/kotlin/dan200/computercraft/gametest/core/ClientTestHooks.kt

package site.siredvin.testiarium.fixture.client

import net.minecraft.client.CloudStatus
import net.minecraft.client.Minecraft
import net.minecraft.client.ParticleStatus
import net.minecraft.client.gui.screens.AccessibilityOnboardingScreen
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.TitleScreen
import net.minecraft.client.tutorial.TutorialSteps
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.Registries
import net.minecraft.gametest.framework.GameTestInfo
import net.minecraft.gametest.framework.GameTestListener
import net.minecraft.gametest.framework.GameTestRegistry
import net.minecraft.gametest.framework.GameTestRunner
import net.minecraft.gametest.framework.GameTestTicker
import net.minecraft.gametest.framework.GlobalTestReporter
import net.minecraft.gametest.framework.MultipleTestTracker
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundSource
import net.minecraft.world.Difficulty
import net.minecraft.world.level.GameRules
import net.minecraft.world.level.GameType
import net.minecraft.world.level.LevelSettings
import net.minecraft.world.level.WorldDataConfiguration
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.level.levelgen.WorldOptions
import net.minecraft.world.level.levelgen.presets.WorldPresets
import org.slf4j.LoggerFactory
import site.siredvin.testiarium.api.Timeouts
import kotlin.system.exitProcess

object ClientTestHooks {
    private const val LEVEL_NAME = "testiarium-client"
    private const val STARTUP_DELAY = 5 * Timeouts.SECOND
    private const val WORLD_SEED = 0L
    private val log = LoggerFactory.getLogger(ClientTestHooks::class.java)
    private val enabled = System.getProperty("testiarium.client") != null
    private var loadedWorld = false
    private var tracker: MultipleTestTracker? = null
    private var startupDelay = STARTUP_DELAY
    private var finished = false

    @JvmStatic
    fun onOpenScreen(screen: Screen): Boolean {
        if (!enabled || loadedWorld || screen !is TitleScreen && screen !is AccessibilityOnboardingScreen) return false
        loadedWorld = true
        openWorld(screen)
        return true
    }

    @JvmStatic
    fun onServerTick(server: MinecraftServer) {
        if (!enabled || finished) return
        val tests = tracker ?: startTests(server) ?: return
        if (server.overworld().gameTime % 20L == 0L) log.info(tests.progressBar)
        if (!tests.isDone) return

        finished = true
        GlobalTestReporter.finish()
        val exitCode = when {
            tests.totalCount == 0 -> 1
            tests.hasFailedRequired() -> 2
            else -> 0
        }
        Minecraft.getInstance().execute {
            val minecraft = Minecraft.getInstance()
            minecraft.level?.disconnect()
            minecraft.clearLevel()
            minecraft.stop()
            exitProcess(exitCode)
        }
    }

    private fun openWorld(screen: Screen) {
        val minecraft = Minecraft.getInstance()
        minecraft.options.autoJump().set(false)
        minecraft.options.cloudStatus().set(CloudStatus.OFF)
        minecraft.options.particles().set(ParticleStatus.MINIMAL)
        minecraft.options.tutorialStep = TutorialSteps.NONE
        minecraft.options.pauseOnLostFocus = false
        minecraft.options.renderDistance().set(6)
        minecraft.options.gamma().set(1.0)
        minecraft.options.getSoundSourceOptionInstance(SoundSource.MUSIC).set(0.0)
        minecraft.options.getSoundSourceOptionInstance(SoundSource.AMBIENT).set(0.0)
        if (minecraft.levelSource.levelExists(LEVEL_NAME)) {
            minecraft.createWorldOpenFlows().loadLevel(screen, LEVEL_NAME)
            return
        }
        val rules = GameRules()
        rules.getRule(GameRules.RULE_DOMOBSPAWNING).set(false, null)
        rules.getRule(GameRules.RULE_DAYLIGHT).set(false, null)
        rules.getRule(GameRules.RULE_WEATHER_CYCLE).set(false, null)
        minecraft.createWorldOpenFlows().createFreshLevel(
            LEVEL_NAME,
            LevelSettings("Testiarium Client Tests", GameType.CREATIVE, false, Difficulty.EASY, true, rules, WorldDataConfiguration.DEFAULT),
            WorldOptions(WORLD_SEED, false, false),
        ) { it.registryOrThrow(Registries.WORLD_PRESET).getOrThrow(WorldPresets.FLAT).createWorldDimensions() }
    }

    private fun startTests(server: MinecraftServer): MultipleTestTracker? {
        if (server.overworld().players().isEmpty()) return null
        server.overworld().players().forEach {
            it.abilities.flying = true
            it.onUpdateAbilities()
            it.connection.teleport(0.0, -30.0, 0.0, 0.0f, 90.0f)
            it.inventory.clearContent()
        }
        if (!Minecraft.getInstance().isRenderingStable()) return null
        if (startupDelay-- >= 0) return null
        return MultipleTestTracker(
            GameTestRunner.runTestBatches(
                GameTestRunner.groupTestsIntoBatches(GameTestRegistry.getAllTestFunctions()),
                BlockPos(0, -60, 0),
                Rotation.NONE,
                server.overworld(),
                GameTestTicker.SINGLETON,
                1,
            ),
        ).also {
            it.addListener(object : GameTestListener {
                private fun cleanup() = server.playerList.players.forEach(ServerPlayer::setupForTest)

                override fun testPassed(test: GameTestInfo) = cleanup()

                override fun testFailed(test: GameTestInfo) = cleanup()

                override fun testStructureLoaded(test: GameTestInfo) = Unit
            })
            tracker = it
        }
    }
}
