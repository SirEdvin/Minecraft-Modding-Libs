// SPDX-FileCopyrightText: 2021 The CC: Tweaked Developers
// SPDX-FileCopyrightText: 2026 SirEdvin
// SPDX-License-Identifier: MPL-2.0
// Adapted from CC:Tweaked 1.113.0 projects/common/src/testMod/java/dan200/computercraft/gametest/core/CCTestCommand.java.

package site.siredvin.testiarium.cct

import com.mojang.brigadier.CommandDispatcher
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands.argument
import net.minecraft.commands.Commands.literal
import net.minecraft.commands.arguments.item.ItemArgument
import net.minecraft.commands.arguments.item.ItemInput
import net.minecraft.network.chat.Component
import net.minecraft.server.MinecraftServer
import net.minecraft.world.level.storage.LevelResource
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.isDirectory
import kotlin.io.path.relativeTo

object CctFixtureCommands {
    fun register(dispatcher: CommandDispatcher<CommandSourceStack>, context: CommandBuildContext) {
        dispatcher.register(
            literal("testiarium").then(
                literal("cct")
                    .then(literal("import").executes { importFiles(it.source.server); 1 })
                    .then(literal("export").executes { exportFiles(it.source.server); 1 })
                    .then(literal("give-computer").then(argument("item", ItemArgument.item(context)).executes {
                        val stack = it.getArgument("item", ItemInput::class.java).createItemStack(1, false)
                        stack.hoverName = Component.literal("testiarium.cct")
                        it.source.playerOrException.addItem(stack)
                        1
                    })),
            ),
        )
    }

    fun importFiles(server: MinecraftServer) = source()?.let { sync(it, destination(server)) }

    private fun exportFiles(server: MinecraftServer) = source()?.let { sync(destination(server), it) }

    private fun source(): Path? = System.getProperty("testiarium.cct-fixtures")?.let(Path::of)

    private fun destination(server: MinecraftServer): Path = server.getWorldPath(LevelResource.ROOT).resolve("computercraft/computer/1")

    private fun sync(from: Path, to: Path) {
        require(from.isDirectory()) { "CCT fixture directory does not exist: $from" }
        Files.walk(from).use { paths ->
            paths.forEach { path ->
                val target = to.resolve(path.relativeTo(from).toString())
                if (Files.isDirectory(path)) Files.createDirectories(target) else {
                    Files.createDirectories(target.parent)
                    Files.copy(path, target, java.nio.file.StandardCopyOption.REPLACE_EXISTING)
                }
            }
        }
    }
}
