// SPDX-FileCopyrightText: 2021 The CC: Tweaked Developers
// SPDX-FileCopyrightText: 2026 SirEdvin
// SPDX-License-Identifier: MPL-2.0
// Adapted from CC:Tweaked 1.113.0 projects/common/src/testMod/{java/kotlin}/dan200/computercraft/gametest/core/{TestAPI,ManagedComputers}.{java,kt}.

package site.siredvin.testiarium.cct

import dan200.computercraft.api.ComputerCraftAPI
import dan200.computercraft.api.lua.IComputerSystem
import dan200.computercraft.api.lua.ILuaAPI
import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.core.lua.CobaltLuaMachine
import dan200.computercraft.core.lua.ILuaMachine
import dan200.computercraft.core.lua.MachineEnvironment
import dan200.computercraft.shared.computer.core.ServerContext
import net.minecraft.gametest.framework.GameTestAssertException
import net.minecraft.server.MinecraftServer
import org.slf4j.LoggerFactory
import java.util.Optional
import java.nio.file.Files
import java.nio.file.Path
import java.io.InputStream
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedDeque

typealias CctComputerAction = IComputerSystem.() -> Unit

object CctComputers {
    private val logger = LoggerFactory.getLogger(CctComputers::class.java)
    private val actions = ConcurrentHashMap<String, ConcurrentLinkedDeque<QueuedAction>>()

    fun initialize() {
        // This must be set before CC:Tweaked creates the server context.
        ServerContext.luaMachine = CctLuaMachineFactory
        ComputerCraftAPI.registerAPIFactory(::CctTestApi)
    }

    fun reset() {
        actions.clear()
        CctComputerState.reset()
    }

    fun enqueue(server: MinecraftServer, label: String, action: CctComputerAction): CctComputerMonitor {
        val queued = QueuedAction(action)
        actions.computeIfAbsent(label) { ConcurrentLinkedDeque() }.add(queued)
        ServerContext.get(server).registry().computers.firstOrNull { it.label == label }?.apply {
            turnOn()
            queueEvent("test_wakeup")
        }
        return CctComputerMonitor(label, queued)
    }

    internal fun run(label: String, computer: IComputerSystem) {
        val queued = actions[label]?.poll() ?: return
        try {
            queued.action.invoke(computer)
            queued.result = Result.success(Unit)
        } catch (error: Throwable) {
            logger.error("Computer $label failed", error)
            queued.result = Result.failure(error)
        }
    }

    internal class QueuedAction(val action: CctComputerAction) {
        @Volatile var result: Result<Unit>? = null
    }

    class CctComputerMonitor internal constructor(private val label: String, private val action: QueuedAction) {
        val isFinished get() = action.result != null

        fun check() {
            action.result?.getOrThrow() ?: throw GameTestAssertException("Computer '$label' did not finish")
        }
    }
}

/** Keeps the machine hook explicit while test actions are dispatched by [CctTestApi]. */
private object CctLuaMachineFactory : ILuaMachine.Factory {
    override fun create(environment: MachineEnvironment, bios: InputStream): ILuaMachine = CobaltLuaMachine(environment, bios)
}

object CctLuaTests {
    fun file(label: String): Path? = System.getProperty("testiarium.cct-fixtures")
        ?.let(Path::of)
        ?.resolve("tests/$label.lua")

    fun require(label: String) {
        val file = file(label) ?: return
        require(Files.isRegularFile(file)) { "No Lua test file for computer '$label': $file" }
    }

}

object CctComputerState {
    const val DONE = "DONE"

    private val states = ConcurrentHashMap<String, State>()

    fun get(label: String): State? = states[label]

    fun reset() = states.clear()

    class State internal constructor() {
        private val markers = ConcurrentHashMap.newKeySet<String>()
        @Volatile private var error: String? = null

        fun isDone(marker: String) = marker in markers

        fun check(marker: String) {
            check(isDone(marker)) { "Computer has not reached $marker" }
            error?.let { throw GameTestAssertException(it) }
        }

        internal fun ok(marker: String) = markers.add(marker)
        internal fun fail(message: String) {
            markers.add(DONE)
            error = message
        }
    }

    internal fun start(label: String): State = State().also { states[label] = it }
}

private class CctTestApi(private val computer: IComputerSystem) : ILuaAPI {
    private lateinit var label: String
    private lateinit var state: CctComputerState.State

    override fun startup() {
        label = computer.label ?: "#${computer.id}"
        state = CctComputerState.start(label)
        CctComputers.run(label, computer)
    }

    override fun shutdown() = Unit

    override fun getNames() = arrayOf("test")

    @LuaFunction
    fun fail(message: String): Nothing {
        state.fail(message)
        throw LuaException(message)
    }

    @LuaFunction
    fun ok(marker: Optional<String>) {
        val actual = marker.orElse(CctComputerState.DONE)
        if (!state.ok(actual)) throw LuaException("Cannot call test.ok twice for $actual")
    }

    @LuaFunction
    fun log(message: String) = LoggerFactory.getLogger(CctTestApi::class.java).info("[Computer '{}'] {}", label, message)
}
