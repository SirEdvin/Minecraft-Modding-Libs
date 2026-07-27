package site.siredvin.testiarium.fixture

import com.mojang.brigadier.CommandDispatcher
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands.literal
import net.minecraft.gametest.framework.GameTestRegistry
import net.minecraft.gametest.framework.StructureUtils
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.block.entity.StructureBlockEntity
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.isDirectory
import kotlin.io.path.relativeTo

/** Testmod-only fixture commands. */
object FixtureCommands {
    fun register(dispatcher: CommandDispatcher<CommandSourceStack>, buildContext: CommandBuildContext) {
        dispatcher.register(
            literal("testiarium")
                .then(
                    literal("import").executes {
                        sync(source(), destination())
                        1
                    },
                )
                .then(
                    literal("export").executes {
                        sync(destination(), source())
                        1
                    },
                )
                .then(
                    literal("regen-structures").executes { context ->
                        sync(source(), destination())
                        GameTestRegistry.getAllTestFunctions().forEach { test ->
                            dispatcher.execute("test export ${test.structureName}", context.source)
                        }
                        sync(destination(), source())
                        1
                    },
                )
                .then(
                    literal("marker").executes { context ->
                        val player = context.source.playerOrException
                        val position = StructureUtils.findNearestStructureBlock(player.blockPosition(), 15, player.serverLevel())
                            ?: return@executes 0
                        val structure = player.level().getBlockEntity(position) as? StructureBlockEntity ?: return@executes 0
                        val test = GameTestRegistry.getTestFunction(structure.structurePath)
                        player.serverLevel().getEntities(EntityType.ARMOR_STAND) { it.isAlive && it.name.string == test.testName }
                            .forEach { it.kill() }
                        EntityType.ARMOR_STAND.create(player.level())?.apply {
                            readAdditionalSaveData(
                                CompoundTag().apply {
                                    putBoolean("Marker", true)
                                    putBoolean("Invisible", true)
                                },
                            )
                            moveTo(player.x, player.y, player.z, player.yRot, player.xRot)
                            customName = Component.literal(test.testName)
                            player.level().addFreshEntity(this)
                        }
                        1
                    },
                ),
        )
    }

    private fun source(): Path = Path.of(requireProperty("testiarium.fixture-source"))

    private fun destination(): Path = Path.of(requireProperty("testiarium.structures"))

    private fun requireProperty(name: String): String = System.getProperty(name) ?: error("Set -D$name to use Testiarium fixture commands")

    private fun sync(from: Path, to: Path) {
        require(from.isDirectory()) { "Fixture directory does not exist: $from" }
        Files.walk(from).use { paths ->
            paths.forEach { path ->
                val target = to.resolve(path.relativeTo(from).toString())
                if (Files.isDirectory(path)) {
                    Files.createDirectories(target)
                } else {
                    Files.createDirectories(target.parent)
                    Files.copy(path, target, java.nio.file.StandardCopyOption.REPLACE_EXISTING)
                }
            }
        }
    }
}
