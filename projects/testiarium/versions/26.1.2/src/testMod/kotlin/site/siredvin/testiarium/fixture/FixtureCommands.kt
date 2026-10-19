package site.siredvin.testiarium.fixture

import com.mojang.brigadier.CommandDispatcher
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands.literal
import net.minecraft.core.registries.Registries
import net.minecraft.gametest.framework.StructureUtils
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.util.ProblemReporter
import net.minecraft.world.entity.EntitySpawnReason
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.block.entity.TestInstanceBlockEntity
import net.minecraft.world.level.storage.TagValueInput
import site.siredvin.testiarium.Testiarium
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
                        context.source.level.registryAccess().lookupOrThrow(Registries.TEST_INSTANCE).forEach { test ->
                            dispatcher.execute("test export ${test.structure()}", context.source)
                        }
                        sync(destination(), source())
                        1
                    },
                )
                .then(
                    literal("marker").executes { context ->
                        val player = context.source.playerOrException
                        val position = StructureUtils.findNearestTest(player.blockPosition(), 15, player.level())
                            .orElse(null) ?: return@executes 0
                        val structure = player.level().getBlockEntity(position) as? TestInstanceBlockEntity ?: return@executes 0
                        val test = structure.test().orElse(null) ?: return@executes 0
                        val testName = Testiarium.testName(test.identifier())
                        player.level().getEntities(EntityType.ARMOR_STAND) { it.isAlive && it.name.string == testName }
                            .forEach { it.discard() }
                        EntityType.ARMOR_STAND.create(player.level(), EntitySpawnReason.COMMAND)?.apply {
                            load(
                                TagValueInput.create(
                                    ProblemReporter.DISCARDING,
                                    player.level().registryAccess(),
                                    CompoundTag().apply {
                                        putBoolean("Marker", true)
                                        putBoolean("Invisible", true)
                                    },
                                ),
                            )
                            snapTo(player.x, player.y, player.z, player.yRot, player.xRot)
                            customName = Component.literal(testName)
                            player.level().addFreshEntity(this)
                        }
                        1
                    },
                ),
        )
    }

    private fun source(): Path = Path.of(requireProperty("testiarium.fixture-source"))

    private fun destination(): Path = Path.of(requireProperty("testiarium.structures")).resolve("data/minecraft/structure")

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
